package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.api.model.Header.IN_REPLY_TO;
import static se.sundsvall.emailsender.api.model.Header.MESSAGE_ID;
import static se.sundsvall.emailsender.api.model.Header.REFERENCES;

import org.junit.jupiter.api.Test;

class HeaderTests {

	@Test
	void enums() {
		assertThat(Header.values()).containsExactlyInAnyOrder(IN_REPLY_TO, REFERENCES, MESSAGE_ID);
	}

	@Test
	void enumValues() {
		assertThat(IN_REPLY_TO.getName()).isEqualTo("In-Reply-To");
		assertThat(REFERENCES.getName()).isEqualTo("References");
		assertThat(MESSAGE_ID.getName()).isEqualTo("Message-ID");
	}
}
