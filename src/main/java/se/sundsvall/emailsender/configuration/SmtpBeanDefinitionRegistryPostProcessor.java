package se.sundsvall.emailsender.configuration;

import static java.util.Objects.nonNull;

import jakarta.validation.Validator;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import se.sundsvall.emailsender.support.CustomJavaMailSenderImpl;

@Component
class SmtpBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, InitializingBean {

	static final String JAVA_MAIL_SENDER_BEAN_NAME = "java-mail-sender-";

	static final String DEFAULT_PROPERTIES = "integration.email.default-properties";
	static final String INSTANCES = "integration.email.instances";

	private ApplicationContext applicationContext;
	private Properties defaultProperties;
	private Map<String, SmtpServerProperties> smtpServerPropertiesByMunicipalityId;

	@Override
	public void afterPropertiesSet() {
		var environment = applicationContext.getEnvironment();
		var validator = applicationContext.getBean(Validator.class);
		var validationBindHandler = new ValidationBindHandler(new SpringValidatorAdapter(validator));
		var binder = Binder.get(environment);

		defaultProperties = binder.bindOrCreate(DEFAULT_PROPERTIES, Bindable.of(Properties.class), validationBindHandler);
		smtpServerPropertiesByMunicipalityId = binder.bind(
			INSTANCES, Bindable.mapOf(String.class, SmtpServerProperties.class), validationBindHandler).get();
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		smtpServerPropertiesByMunicipalityId.forEach((municipalityId, smtpServerProperties) -> {
			// Merge the default properties with the SMTP server properties, with
			// values from the latter possibly overriding defaults
			var mergedProperties = new Properties();
			mergedProperties.putAll(defaultProperties);
			if (nonNull(smtpServerProperties.properties())) {
				mergedProperties.putAll(smtpServerProperties.properties());
			}

			var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(CustomJavaMailSenderImpl.class)
				.addPropertyValue("municipalityId", municipalityId)
				.addPropertyValue("host", smtpServerProperties.host())
				.addPropertyValue("port", smtpServerProperties.port())
				.addPropertyValue("username", smtpServerProperties.username())
				.addPropertyValue("password", smtpServerProperties.password())
				.addPropertyValue("javaMailProperties", mergedProperties)
				.getBeanDefinition();

			registry.registerBeanDefinition(JAVA_MAIL_SENDER_BEAN_NAME + municipalityId, beanDefinition);
		});
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
