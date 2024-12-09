package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("junit")
class SendEmailRequestTests {

	@Test
	void testGettersAndSetters() {
		final var request = new SendEmailRequest();
		request.setSender(new SendEmailRequest.Sender());
		request.setEmailAddress("someEmailAddress");
		request.setSubject("someSubject");
		request.setMessage("someMessage");
		request.setHtmlMessage("someHtmlMessage");
		request.setAttachments(List.of(new SendEmailRequest.Attachment()));

		assertThat(request.getSender()).isNotNull();
		assertThat(request.getEmailAddress()).isEqualTo("someEmailAddress");
		assertThat(request.getSubject()).isEqualTo("someSubject");
		assertThat(request.getMessage()).isEqualTo("someMessage");
		assertThat(request.getHtmlMessage()).isEqualTo("someHtmlMessage");
		assertThat(request.getAttachments()).hasSize(1);
	}

	@Test
	void testBuilder() {
		final var request = SendEmailRequest.builder()
			.withEmailAddress("someEmailAddress")
			.withSubject("someSubject")
			.withMessage("someMessage")
			.withHtmlMessage("someHtmlMessage")
			.withSender(SendEmailRequest.Sender.builder()
				.withAddress("someAddress")
				.withName("someName")
				.withReplyTo("someReplyTo")
				.build())
			.withAttachments(List.of(
				SendEmailRequest.Attachment.builder()
					.withName("someName")
					.withContent("someContent")
					.withContentType("someContentType")
					.build()))
			.build();

		assertThat(request.getSender()).isNotNull();
		assertThat(request.getEmailAddress()).isEqualTo("someEmailAddress");
		assertThat(request.getSubject()).isEqualTo("someSubject");
		assertThat(request.getMessage()).isEqualTo("someMessage");
		assertThat(request.getHtmlMessage()).isEqualTo("someHtmlMessage");
		assertThat(request.getSender()).satisfies(sender -> {
			assertThat(sender.getName()).isEqualTo("someName");
			assertThat(sender.getAddress()).isEqualTo("someAddress");
			assertThat(sender.getReplyTo()).isEqualTo("someReplyTo");
		});
		assertThat(request.getAttachments()).satisfies(attachments -> {
			assertThat(attachments).hasSize(1);
			assertThat(attachments.getFirst().getName()).isEqualTo("someName");
			assertThat(attachments.getFirst().getContent()).isEqualTo("someContent");
			assertThat(attachments.getFirst().getContentType()).isEqualTo("someContentType");
		});
	}
}
