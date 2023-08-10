package se.sundsvall.emailsender.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

	@Mock
	private JavaMailSender mockMailSender;

	@Mock
	private MimeMessage mockMimeMessage;

	@InjectMocks
	private EmailService service;

	@Test
	void sendEmail_withValidRequest_returnsTrue() throws MessagingException {
		final var request = createValidEmailRequest();
		when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

		service.sendMail(request);

		verify(mockMailSender, times(1)).send(mockMimeMessage);
		verifyNoMoreInteractions(mockMailSender);
		verify(mockMimeMessage, times(1)).setFrom(any(String.class));
		verify(mockMimeMessage, times(1)).setReplyTo(any(Address[].class));
		verify(mockMimeMessage, times(1)).setRecipients(eq(Message.RecipientType.TO), any(String.class));
		verify(mockMimeMessage, times(1)).setSubject(any(String.class), any(String.class));
		verify(mockMimeMessage, times(1)).setContent(any(Multipart.class));
		verifyNoMoreInteractions(mockMimeMessage);
	}
}
