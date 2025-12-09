package se.sundsvall.emailsender.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.jilt.Builder;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;
import se.sundsvall.emailsender.api.validation.ValidHeaders;

@Builder(setterPrefix = "with", factoryMethod = "create", toBuilder = "from")
@Schema(description = "The request class for sending an e-mail")
public record SendEmailRequest(

	@NotBlank @Email @Schema(description = "Recipient e-mail address", examples = "recipient@recipient.se") String emailAddress,

	@NotBlank @Schema(description = "E-mail subject") String subject,

	@Schema(description = "E-mail plain-text body") String message,

	@ValidBase64(nullable = true) @Schema(description = "E-mail HTML body (BASE64-encoded)") String htmlMessage,

	@Valid @NotNull Sender sender,

	List<@Valid Attachment> attachments,

	@ValidHeaders @Schema(description = "Headers") Map<@NotBlank String, @NotEmpty List<String>> headers) {

	@Builder(setterPrefix = "with", factoryMethod = "create", toBuilder = "from")
	@Schema(description = "Attachment")
	public record Attachment(

		@ValidBase64 @Schema(description = "The attachment (file) content as a BASE64-encoded string", examples = "aGVsbG8gd29ybGQK") String content,

		@NotBlank @Schema(description = "The attachment filename", examples = "test.txt") String name,

		@NotBlank @Schema(description = "The attachment content type", examples = "text/plain") String contentType) {}

	@Builder(setterPrefix = "with", factoryMethod = "create", toBuilder = "from")
	@Schema(description = "E-mail sender")
	public record Sender(

		@NotBlank @Schema(description = "The sender of the e-mail") String name,

		@Email @NotBlank @Schema(description = "Sender e-mail address", examples = "sender@sender.se") String address,

		@Email @Schema(description = "Reply-to e-mail address", examples = "sender@sender.se") String replyTo) {}
}
