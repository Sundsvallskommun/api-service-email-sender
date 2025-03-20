package se.sundsvall.emailsender.configuration;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class SmtpConfiguration {

	@Bean
	SmtpBeanDefinitionRegistryPostProcessor smtpBeanDefinitionRegistryPostProcessor(final Environment environment, final Validator validator) {
		return new SmtpBeanDefinitionRegistryPostProcessor(environment, validator);
	}
}
