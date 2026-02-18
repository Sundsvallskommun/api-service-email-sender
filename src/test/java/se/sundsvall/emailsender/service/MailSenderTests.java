package se.sundsvall.emailsender.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

import static org.assertj.core.api.Assertions.assertThat;

class MailSenderTests {

	static class DummyMailSender extends AbstractMailSender {

		@Override
		public void sendEmail(final SendEmailRequest request) {
			// Do nothing, as real implementations of the sendEmail method is tested elsewhere
		}
	}

	private final DummyMailSender mailSender = new DummyMailSender();

	@Test
	void municipalityId() {
		var municipalityId = "someMunicipalityId";

		mailSender.setMunicipalityId(municipalityId);

		assertThat(mailSender.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void decodeBase64() {
		var validBase64 = "c29tZVZhbGlkQmFzZTY0U3RyaW5n";
		var invalidBase64 = "%invalid-base-64-data%";

		assertThat(mailSender.decodeBase64(validBase64)).hasValue("someValidBase64String");
		assertThat(mailSender.decodeBase64(invalidBase64)).isEmpty();
	}

	@Test
	void formatHeader() {
		var strings = List.of("<abc@abc>", "<bac@bac>", "<cab@cab>");

		var result = mailSender.formatHeader(strings);

		assertThat(result).isEqualTo("<abc@abc> <bac@bac> <cab@cab>");
	}
}
