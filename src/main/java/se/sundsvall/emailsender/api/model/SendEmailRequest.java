package se.sundsvall.emailsender.api.model;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with", toBuilder = true)
@Getter
@Setter
@ToString
public class SendEmailRequest {
    
	@Schema(description = "Recipient e-mail address", example = "recipient@recipient.se" )
    @NotBlank
    @Email
    private String emailAddress;

    @Schema(description = "E-mail subject")
    @NotBlank
    private String subject;

    @Schema(description = "E-mail plain-text body")
    private String message;

    @Schema(description = "E-mail HTML body (BASE64-encoded)")
    private String htmlMessage;

    @Schema(description = "Sender name")
    @NotBlank
    private String senderName;

    @Schema(description = "Sender e-mail address", example = "sender@sender.se")
    @NotBlank
    @Email
    private String senderEmail;

    @Schema(description = "Attachments")
    private List<Attachment> attachments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @Builder(setterPrefix = "with")
    public static class Attachment {

        @Schema(description = "The attachment (file) content as a BASE64-encoded string", example = "aGVsbG8gd29ybGQK")
        private String content;

        @Schema(description = "The attachment filename", example = "test.txt")
        private String name;

        @Schema(description = "The attachment content type", example = "text/plain")
        private String contentType;
    }
}
