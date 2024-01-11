package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.api.model.Header.IN_REPLY_TO;
import static se.sundsvall.emailsender.api.model.Header.REFERENCES;

import org.junit.jupiter.api.Test;

class HeaderTest {

	@Test
	void enums() {
		assertThat(Header.values()).containsExactlyInAnyOrder(IN_REPLY_TO, REFERENCES);
	}

	@Test
	void enumValues() {
		assertThat(IN_REPLY_TO).hasToString("IN_REPLY_TO");
		assertThat(REFERENCES).hasToString("REFERENCES");
	}
}

