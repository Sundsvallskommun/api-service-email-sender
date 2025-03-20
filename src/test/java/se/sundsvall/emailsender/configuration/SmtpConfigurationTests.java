package se.sundsvall.emailsender.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.emailsender.Application;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class)
class SmtpConfigurationTests {

	@Autowired
	private SmtpBeanDefinitionRegistryPostProcessor smtpBeanDefinitionRegistryPostProcessor;

	@Test
	void smtpBeanDefinitionRegistryPostProcessorBean_isNotNull() {
		assertThat(smtpBeanDefinitionRegistryPostProcessor).isNotNull();
	}
}
