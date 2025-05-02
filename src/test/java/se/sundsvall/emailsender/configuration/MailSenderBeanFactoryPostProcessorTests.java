package se.sundsvall.emailsender.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.emailsender.Application;
import se.sundsvall.emailsender.service.MailSender;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class)
class MailSenderBeanFactoryPostProcessorTests {

	@Autowired
	private MailSenderBeanFactoryPostProcessor mailSenderBeanFactoryPostProcessor;

	@Autowired
	private List<MailSender> mailSenders;

	@Test
	void smtpBeanDefinitionRegistryPostProcessorBean_isNotNull() {
		assertThat(mailSenderBeanFactoryPostProcessor).isNotNull();
	}

	@Test
	void twoMailSenderBeansAreRegistered() {
		assertThat(mailSenders).hasSize(2);
	}

	@Nested
	class MailSenderPropertiesTest {

		@Test
		void creationAndAccessors() {
			final var properties = new Properties();
			properties.put("someKey", "someValue");
			final var basic = new MailSenderBeanFactoryPostProcessor.MailSenderProperties.Basic("someHost", 1234, "someUsername", "somePassword", properties);
			final var azure = new MailSenderBeanFactoryPostProcessor.MailSenderProperties.Azure("someTenantId", "someClientId", "someClientSecret", "someScope");

			final var smtpServerProperties = new MailSenderBeanFactoryPostProcessor.MailSenderProperties(basic, azure);

			assertThat(smtpServerProperties.basic()).isEqualTo(basic);
			assertThat(smtpServerProperties.azure()).isEqualTo(azure);
		}
	}
}
