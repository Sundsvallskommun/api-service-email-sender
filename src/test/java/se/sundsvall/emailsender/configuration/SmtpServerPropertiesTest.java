package se.sundsvall.emailsender.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class SmtpServerPropertiesTest {

	@Test
	void creationAndAccessors() {
		var host = "someHost";
		var port = 1234;
		var username = "someUsername";
		var password = "somePassword";
		var properties = new Properties();
		properties.put("someKey", "someValue");

		var smtpServerProperties = new SmtpServerProperties(host, port, username, password, properties);

		assertThat(smtpServerProperties.host()).isEqualTo(host);
		assertThat(smtpServerProperties.port()).isEqualTo(port);
		assertThat(smtpServerProperties.username()).isEqualTo(username);
		assertThat(smtpServerProperties.password()).isEqualTo(password);
		assertThat(smtpServerProperties.properties()).isEqualTo(properties);
	}
}
