package se.sundsvall.emailsender.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;
import se.sundsvall.emailsender.api.validation.ValidMessageId;

@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Schema(description = "The request class for sending an e-mail")
public class SendEmailRequest {

	@Schema(description = "Recipient e-mail address", example = "recipient@recipient.se")
	@NotBlank
	@Email
	private String emailAddress;

	@Schema(description = "E-mail subject")
	@NotBlank
	private String subject;

	@Schema(description = "E-mail plain-text body")
	private String message;

	@Schema(description = "E-mail HTML body (BASE64-encoded)")
	@ValidBase64(nullable = true)
	private String htmlMessage;

	@Valid
	@NotNull
	private Sender sender;

	private List<@Valid Attachment> attachments;

	@Schema(description = "Headers")
	private Map<Header, List<@ValidMessageId String>> headers;

	@Getter
	@Setter
	@Builder(setterPrefix = "with")
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Schema(description = "E-mail attachment")
	public static class Attachment {

		@Schema(description = "The attachment (file) content as a BASE64-encoded string", example = "aGVsbG8gd29ybGQK")
		@ValidBase64
		private String content;

		@Schema(description = "The attachment filename", example = "test.txt")
		@NotBlank
		private String name;

		@Schema(description = "The attachment content type", example = "text/plain")
		@NotBlank
		private String contentType;
	}

	@Getter
	@Setter
	@Builder(setterPrefix = "with")
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Schema(description = "E-mail sender")
	public static class Sender {

		@NotBlank
		@Schema(description = "The sender of the e-mail")
		private String name;

		@Email
		@NotBlank
		@Schema(description = "Sender e-mail address", example = "sender@sender.se")
		private String address;

		@Email
		@Schema(description = "Reply-to e-mail address", example = "sender@sender.se")
		private String replyTo;
	}

}
