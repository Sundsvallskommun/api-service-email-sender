package se.sundsvall.emailsender;

import static se.sundsvall.emailsender.api.model.Header.IN_REPLY_TO;
import static se.sundsvall.emailsender.api.model.Header.MESSAGE_ID;
import static se.sundsvall.emailsender.api.model.Header.REFERENCES;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import se.sundsvall.emailsender.api.model.SendEmailRequest;

public final class TestDataFactory {

	private TestDataFactory() {}

	public static SendEmailRequest createValidEmailRequest() {
		return createValidEmailRequest(null);
	}

	public static SendEmailRequest createValidEmailRequest(final Consumer<SendEmailRequest> modifier) {
		final var attachment = SendEmailRequest.Attachment.builder()
			.withContent(Base64.getEncoder().encodeToString("someContent".getBytes()))
			.withName("someName")
			.withContentType("image/jpg")
			.build();

		final var sender = SendEmailRequest.Sender.builder()
			.withName("someName")
			.withAddress("receiver@receiver.com")
			.withReplyTo("receiver@receiver.com")
			.build();

		final var request = SendEmailRequest.builder()
			.withEmailAddress("receiver@receiver.com")
			.withSubject("subject")
			.withMessage("message")
			.withHtmlMessage("htmlMessage")
			.withSender(sender)
			.withHeaders(Map.of(
				MESSAGE_ID, List.of("<318d3a5c-cd45-45ef-94a0-0e3a88e47bf6@sundsvall.se>"),
				IN_REPLY_TO, List.of("<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>"),
				REFERENCES, List.of("<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>")))
			.withAttachments(List.of(attachment))
			.build();

		if (modifier != null) {
			modifier.accept(request);
		}

		return request;
	}

	public static SendEmailRequest.Sender createValidSender() {
		return createValidSender(null);
	}

	public static SendEmailRequest.Sender createValidSender(final Consumer<SendEmailRequest.Sender> modifier) {
		final var sender = SendEmailRequest.Sender.builder()
			.withName("Sundsvalls Kommun")
			.withAddress("info@sundsvall.se")
			.withReplyTo("support@sundsvall.se")
			.build();

		if (modifier != null) {
			modifier.accept(sender);
		}

		return sender;
	}

	public static SendEmailRequest.Attachment createValidAttachment() {
		return createValidAttachment(null);
	}

	public static SendEmailRequest.Attachment createValidAttachment(final Consumer<SendEmailRequest.Attachment> modifier) {
		final var attachment = SendEmailRequest.Attachment.builder()
			.withName("Sundsvalls Kommun")
			.withContent("someContent")
			.withContentType("application/pdf")
			.build();

		if (modifier != null) {
			modifier.accept(attachment);
		}

		return attachment;
	}
}
