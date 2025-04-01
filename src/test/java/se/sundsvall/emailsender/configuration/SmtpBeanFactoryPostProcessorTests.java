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
class SmtpBeanFactoryPostProcessorTests {

	@Autowired
	private SmtpBeanFactoryPostProcessor smtpBeanFactoryPostProcessor;

	@Autowired
	private List<MailSender> mailSenders;

	@Test
	void smtpBeanDefinitionRegistryPostProcessorBean_isNotNull() {
		assertThat(smtpBeanFactoryPostProcessor).isNotNull();
	}

	@Test
	void twoMailSenderBeansAreRegistered() {
		assertThat(mailSenders).hasSize(2);
	}

	@Nested
	class SmtpServerPropertiesTest {

		@Test
		void creationAndAccessors() {
			var properties = new Properties();
			properties.put("someKey", "someValue");
			var basic = new SmtpBeanFactoryPostProcessor.SmtpServerProperties.Basic("someHost", 1234, "someUsername", "somePassword", properties);
			var azure = new SmtpBeanFactoryPostProcessor.SmtpServerProperties.Azure("someSendAsId", "someTenantId", "someClientId", "someClientSecret", "someScope");

			var smtpServerProperties = new SmtpBeanFactoryPostProcessor.SmtpServerProperties(basic, azure);

			assertThat(smtpServerProperties.basic()).isEqualTo(basic);
			assertThat(smtpServerProperties.azure()).isEqualTo(azure);
		}
	}
}
