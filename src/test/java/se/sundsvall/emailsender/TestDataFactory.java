package se.sundsvall.emailsender;

import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.api.model.Sender;

public final class TestDataFactory {

    private TestDataFactory() { }

    public static SendEmailRequest createValidEmailRequest() {
        return createValidEmailRequest(null);
    }

    public static SendEmailRequest createValidEmailRequest(final Consumer<SendEmailRequest> modifier) {
        var attachment = SendEmailRequest.Attachment.builder()
            .withContent(Base64.getEncoder().encodeToString("someContent".getBytes()))
            .withName("someName")
            .withContentType("image/jpg")
            .build();

        var request = SendEmailRequest.builder()
            .withEmailAddress("receiver@receiver.com")
            .withSubject("subject")
            .withMessage("message")
            .withHtmlMessage("htmlMessage")
            .withSender(createValidSender())
            .withAttachments(List.of(attachment))
            .build();

        if (modifier != null) {
            modifier.accept(request);
        }

        return request;
    }

    public static Sender createValidSender() {
        return createValidSender(null);
    }

    public static Sender createValidSender(final Consumer<Sender> modifier) {
        var sender= Sender.builder()
            .withName("Sundsvalls Kommun")
            .withAddress("info@sundsvall.se")
            .withReplyTo("support@sundsvall.se")
            .build();

        if (modifier != null) {
            modifier.accept(sender);
        }

        return sender;
    }
}
