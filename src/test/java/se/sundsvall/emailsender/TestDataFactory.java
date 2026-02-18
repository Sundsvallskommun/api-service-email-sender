package se.sundsvall.emailsender;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import se.sundsvall.emailsender.api.model.AttachmentBuilder;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.api.model.SendEmailRequestBuilder;
import se.sundsvall.emailsender.api.model.SenderBuilder;

import static se.sundsvall.emailsender.api.model.Header.AUTO_SUBMITTED;
import static se.sundsvall.emailsender.api.model.Header.IN_REPLY_TO;
import static se.sundsvall.emailsender.api.model.Header.MESSAGE_ID;
import static se.sundsvall.emailsender.api.model.Header.REFERENCES;

public final class TestDataFactory {

	private TestDataFactory() {}

	public static SendEmailRequest createValidSendEmailRequest() {
		var attachment = AttachmentBuilder.create()
			.withContent(Base64.getEncoder().encodeToString("someContent".getBytes()))
			.withName("someName")
			.withContentType("image/jpg")
			.build();

		var sender = SenderBuilder.create()
			.withName("someName")
			.withAddress("receiver@receiver.com")
			.withReplyTo("replyTo@receiver.com")
			.build();

		return SendEmailRequestBuilder.create()
			.withEmailAddress("receiver@receiver.com")
			.withSubject("subject")
			.withMessage("message")
			.withHtmlMessage("htmlMessage")
			.withSender(sender)
			.withHeaders(Map.of(
				MESSAGE_ID.getKey(), List.of("<318d3a5c-cd45-45ef-94a0-0e3a88e47bf6@sundsvall.se>"),
				IN_REPLY_TO.getKey(), List.of("<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>"),
				REFERENCES.getKey(), List.of("<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>"),
				AUTO_SUBMITTED.getKey(), List.of("auto-generated")))
			.withAttachments(List.of(attachment))
			.build();
	}

	public static SendEmailRequest.Sender createValidSender() {
		return SenderBuilder.create()
			.withName("Sundsvalls Kommun")
			.withAddress("info@sundsvall.se")
			.withReplyTo("support@sundsvall.se")
			.build();
	}

	public static SendEmailRequest.Attachment createValidAttachment() {
		return AttachmentBuilder.create()
			.withName("Sundsvalls Kommun")
			.withContent("someContent")
			.withContentType("application/pdf")
			.build();
	}
}
