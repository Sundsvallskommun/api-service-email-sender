package se.sundsvall.emailsender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.emailsender.api.domain.EmailRequest;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private final static String EMAIL_SENDER = "sender@sender.com";
    private final static String EMAIL_RECEIVER = "receiver@receiver.com";

    private EmailService service;

    @Mock
    private JavaMailSender mockSender;

    @Mock
    private MimeMessageHelper helper;

    private MimeMessage mimeMessage;

    @BeforeEach
    void init() {
        mockSender.createMimeMessage();
        mimeMessage = new MimeMessage((Session) null);
        service = new EmailService(mockSender);
    }

    @Test
    void sendEmail_withValidRequest_returnsTrue() throws MessagingException {
        var request = validEmailRequest(null);
        when(mockSender.createMimeMessage()).thenReturn(mimeMessage);

        var result = service.sendMail(request);
        assertThat(result).isTrue();

        verify(mockSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendEmail_withinvalidAttachment_returns() throws MessagingException {
        String invalidBase64 = "not a base 64 string";
        var invalidAttachment = EmailRequest.Attachment.builder()
            .withContent(invalidBase64)
            .withName("attachment")
            .withContentType("image/jpg")
            .build();

        var request = validEmailRequest((req) -> {
            req.setAttachments(List.of(invalidAttachment));
            req.setHtmlMessage(null);
        });
        service.createMessage(helper, request);

        verify(helper, never()).addAttachment(anyString(), any(DataSource.class));
    }

    @Test
    void getOrEmptyAttatchment_givenEmptyListOfAttatchment_returnEmptyList() throws MessagingException {
        var request = validEmailRequest((req) -> {
            req.setAttachments(null);
        });
        service.createMessage(helper, request);

        verify(helper, never()).addAttachment(anyString(), any(DataSource.class));
    }

    private EmailRequest validEmailRequest(Consumer<EmailRequest> modifier) {
        var attachment = EmailRequest.Attachment.builder()
            .withContent(Base64.getEncoder().encodeToString("content".getBytes()))
            .withName("attachment")
            .withContentType("image/jpg")
            .build();
        var request = EmailRequest.builder()
            .withEmailAddress(EMAIL_RECEIVER)
            .withSubject("subject")
            .withMessage("message")
            .withHtmlMessage("htmlMessage")
            .withSenderName("senderName")
            .withSenderEmail(EMAIL_SENDER)
            .withAttachments(List.of(attachment))
            .build();

        if (modifier != null) {
            modifier.accept(request);
        }

        return request;
    }
}
