package se.sundsvall.emailsender.configuration;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import se.sundsvall.emailsender.service.MicrosoftGraphMailSender;
import se.sundsvall.emailsender.service.SmtpMailSender;

@Component
class SmtpBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, InitializingBean {

	static final String SMTP_MAIL_SENDER_BEAN_NAME = "smtp-mail-sender-";
	static final String MICROSOFT_GRAPH_MAIL_SENDER_BEAN_NAME = "ms-graph-mail-sender-";

	static final String DEFAULT_PROPERTIES = "integration.email.default-properties";
	static final String INSTANCES = "integration.email.instances";

	private Environment environment;
	private Validator validator;
	private Properties defaultProperties;
	private Map<String, SmtpServerProperties> smtpServerPropertiesByMunicipalityId;

	@Override
	public void afterPropertiesSet() {
		var validationBindHandler = new ValidationBindHandler(new SpringValidatorAdapter(validator));
		var binder = Binder.get(environment);

		defaultProperties = binder.bindOrCreate(DEFAULT_PROPERTIES, Bindable.of(Properties.class), validationBindHandler);
		smtpServerPropertiesByMunicipalityId = binder.bind(
			INSTANCES, Bindable.mapOf(String.class, SmtpServerProperties.class), validationBindHandler).get();
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		var beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

		smtpServerPropertiesByMunicipalityId.forEach((municipalityId, smtpServerProperties) -> {
			var basicSet = nonNull(smtpServerProperties.basic);
			var azureSet = nonNull(smtpServerProperties.azure);

			// Make sure that exactly one of "basic" and "azure" is set, and validate the one that actually is
			if ((!basicSet && !azureSet) || (basicSet && azureSet)) {
				throw new BeanCreationException("Exactly one of SMTP 'basic' or 'azure' properties must be set");
			} else if (basicSet) {
				validator.validate(smtpServerProperties.basic);

				// Merge the default properties with the SMTP server properties, with
				// values from the latter possibly overriding defaults
				var mergedProperties = new Properties();
				mergedProperties.putAll(defaultProperties);
				if (nonNull(smtpServerProperties.basic.properties)) {
					mergedProperties.putAll(smtpServerProperties.basic.properties);
				}

				registerSmtpMailSender(beanDefinitionRegistry, municipalityId, smtpServerProperties, mergedProperties);
			} else {
				validator.validate(smtpServerProperties.azure);

				registerMicrosoftGraphMailSender(beanDefinitionRegistry, municipalityId, smtpServerProperties);
			}
		});
	}

	void registerSmtpMailSender(final BeanDefinitionRegistry beanDefinitionRegistry, final String municipalityId, final SmtpServerProperties smtpServerProperties, final Properties mergedProperties) {
		var javaMailSender = new NoOpOnUpdateMessageIdJavaMailSender();
		javaMailSender.setHost(smtpServerProperties.basic.host);
		javaMailSender.setPort(smtpServerProperties.basic.port);
		ofNullable(smtpServerProperties.basic.username).ifPresent(javaMailSender::setUsername);
		ofNullable(smtpServerProperties.basic.password).ifPresent(javaMailSender::setPassword);
		javaMailSender.setJavaMailProperties(mergedProperties);

		var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SmtpMailSender.class)
			.addConstructorArgValue(javaMailSender)
			.addPropertyValue("municipalityId", municipalityId)
			.getBeanDefinition();

		registerBeanDefinition(beanDefinitionRegistry, SMTP_MAIL_SENDER_BEAN_NAME + municipalityId, beanDefinition);
	}

	void registerMicrosoftGraphMailSender(final BeanDefinitionRegistry beanDefinitionRegistry, final String municipalityId, final SmtpServerProperties smtpServerProperties) {
		var clientSecretCredential = new ClientSecretCredentialBuilder()
			.tenantId(smtpServerProperties.azure.tenantId)
			.clientId(smtpServerProperties.azure.clientId)
			.clientSecret(smtpServerProperties.azure.clientSecret)
			.build();
		var graphServiceClient = new GraphServiceClient(clientSecretCredential, smtpServerProperties.azure.scope);

		var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MicrosoftGraphMailSender.class)
			.addConstructorArgValue(graphServiceClient)
			.addConstructorArgValue(smtpServerProperties.azure.sendAsId)
			.addPropertyValue("municipalityId", municipalityId)
			.getBeanDefinition();

		registerBeanDefinition(beanDefinitionRegistry, MICROSOFT_GRAPH_MAIL_SENDER_BEAN_NAME + municipalityId, beanDefinition);
	}

	void registerBeanDefinition(final BeanDefinitionRegistry beanDefinitionRegistry, final String beanName, final BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		environment = applicationContext.getEnvironment();
		validator = applicationContext.getBean(Validator.class);
	}

	/**
	 * The default implementation of {@link JavaMailSenderImpl} generates a new
	 * message-id before sending messages. Since this is an unwanted behaviour,
	 * we are overriding the MimeMessage#updateMessageID method to cancel the
	 * message-id generation.
	 */
	static class NoOpOnUpdateMessageIdJavaMailSender extends JavaMailSenderImpl {

		@Override
		public MimeMessage createMimeMessage() {
			return new NoOpOnUpdateMessageIdMimeMessage(getSession());
		}
	}

	static class NoOpOnUpdateMessageIdMimeMessage extends MimeMessage {

		NoOpOnUpdateMessageIdMimeMessage(final Session session) {
			super(session);
		}

		@Override
		protected void updateMessageID() {
			// Do nothing, to prevent the message-id from being overwritten
		}
	}

	@Validated
	record SmtpServerProperties(

		Basic basic,
		Azure azure) {

		private static final String NOT_BLANK_MESSAGE = "must not be blank";

		record Basic(
			@NotBlank(message = NOT_BLANK_MESSAGE) String host,
			@DefaultValue("25") Integer port,
			String username,
			String password,
			Properties properties) {}

		record Azure(
			@NotBlank(message = NOT_BLANK_MESSAGE) String sendAsId,
			@NotBlank(message = NOT_BLANK_MESSAGE) String tenantId,
			@NotBlank(message = NOT_BLANK_MESSAGE) String clientId,
			@NotBlank(message = NOT_BLANK_MESSAGE) String clientSecret,
			@DefaultValue("https://graph.microsoft.com/.default") String scope) {}
	}
}
