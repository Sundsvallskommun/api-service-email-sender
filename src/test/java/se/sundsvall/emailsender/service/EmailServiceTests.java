package se.sundsvall.emailsender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.api.model.Sender;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    private final static String EMAIL_SENDER = "sender@sender.com";
    private final static String EMAIL_RECEIVER = "receiver@receiver.com";

    @Mock
    private JavaMailSender mockMailSender;
    @Mock
    private MimeMessage mockMimeMessage;

    private EmailService service;

    @BeforeEach
    void init() {
        service = new EmailService(mockMailSender);
    }

    @Test
    void sendEmail_withValidRequest_returnsTrue() throws MessagingException {
        var request = validEmailRequest(null);
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        var result = service.sendMail(request);
        assertThat(result).isTrue();

        verify(mockMailSender, times(1)).send(mockMimeMessage);
        verifyNoMoreInteractions(mockMailSender);
        verify(mockMimeMessage, times(1)).setFrom(any(String.class));
        verify(mockMimeMessage, times(1)).setReplyTo(any(Address[].class));
        verify(mockMimeMessage, times(1)).setRecipients(eq(Message.RecipientType.TO), any(String.class));
        verify(mockMimeMessage, times(1)).setSubject(any(String.class), any(String.class));
        verify(mockMimeMessage, times(1)).setContent(any(Multipart.class));
        verifyNoMoreInteractions(mockMimeMessage);
    }

    private SendEmailRequest validEmailRequest(final Consumer<SendEmailRequest> modifier) {
        var attachment = SendEmailRequest.Attachment.builder()
            .withContent(Base64.getEncoder().encodeToString("content".getBytes()))
            .withName("attachment")
            .withContentType("image/jpg")
            .build();
        var request = SendEmailRequest.builder()
            .withEmailAddress(EMAIL_RECEIVER)
            .withSubject("subject")
            .withMessage("message")
            .withHtmlMessage("htmlMessage")
            .withSender(Sender.builder()
                .withName("senderName")
                .withAddress(EMAIL_SENDER)
                .withReplyTo("replyTo@sender.com")
                .build())
            .withAttachments(List.of(attachment))
            .build();

        if (modifier != null) {
            modifier.accept(request);
        }

        return request;
    }
}
