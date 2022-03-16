package se.sundsvall.emailsender.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.emailsender.api.domain.EmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    private final static String EMAIL_SENDER = "sender@sender.com";
    private final static String EMAIL_RECEIVER = "receiver@receiver.com";

    @Mock
    private EmailService mockService;

    private EmailController emailController;

    @BeforeEach
    public void setUp() {
        emailController = new EmailController(mockService);
    }

    @Test
    void sendMail_givenValidDto_shouldReturn_200_OK() throws Exception {
        when(mockService.sendMail(any(EmailRequest.class))).thenReturn(true);

        var result = emailController.sendMail(validEmailRequest());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(mockService,times(1)).sendMail(any(EmailRequest.class));
    }

    private EmailRequest validEmailRequest() {
        var attachment = EmailRequest.Attachment.builder()
            .withContent(Base64.getEncoder().encodeToString("content".getBytes()))
            .withName("attatchment")
            .withContentType("image/jpg")
            .build();

        return EmailRequest.builder()
            .withEmailAddress(EMAIL_RECEIVER)
            .withSubject("subject")
            .withMessage("message")
            .withHtmlMessage("htmlMessage")
            .withSenderName("senderName")
            .withSenderEmail(EMAIL_SENDER)
            .withAttachments(List.of(attachment))
            .build();
    }
}
