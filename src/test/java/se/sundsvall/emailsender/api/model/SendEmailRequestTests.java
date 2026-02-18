package se.sundsvall.emailsender.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("junit")
class SendEmailRequestTests {

	@Test
	void testBuilder() {
		var request = SendEmailRequestBuilder.create()
			.withEmailAddress("someEmailAddress")
			.withSubject("someSubject")
			.withMessage("someMessage")
			.withHtmlMessage("someHtmlMessage")
			.withSender(SenderBuilder.create()
				.withAddress("someAddress")
				.withName("someName")
				.withReplyTo("someReplyTo")
				.build())
			.withAttachments(List.of(
				AttachmentBuilder.create()
					.withName("someName")
					.withContent("someContent")
					.withContentType("someContentType")
					.build()))
			.build();

		assertThat(request.sender()).isNotNull();
		assertThat(request.emailAddress()).isEqualTo("someEmailAddress");
		assertThat(request.subject()).isEqualTo("someSubject");
		assertThat(request.message()).isEqualTo("someMessage");
		assertThat(request.htmlMessage()).isEqualTo("someHtmlMessage");
		assertThat(request.sender()).satisfies(sender -> {
			assertThat(sender.name()).isEqualTo("someName");
			assertThat(sender.address()).isEqualTo("someAddress");
			assertThat(sender.replyTo()).isEqualTo("someReplyTo");
		});
		assertThat(request.attachments()).satisfies(attachments -> {
			assertThat(attachments).hasSize(1);
			assertThat(attachments.getFirst().name()).isEqualTo("someName");
			assertThat(attachments.getFirst().content()).isEqualTo("someContent");
			assertThat(attachments.getFirst().contentType()).isEqualTo("someContentType");
		});
	}
}
