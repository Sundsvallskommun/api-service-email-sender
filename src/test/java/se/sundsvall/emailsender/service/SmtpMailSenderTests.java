package se.sundsvall.emailsender.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.zalando.problem.ThrowableProblem;

@ExtendWith(MockitoExtension.class)
class SmtpMailSenderTests {

	@Mock
	private JavaMailSender mockJavaMailSender;
	@Mock
	private MimeMessage mockMimeMessage;

	@InjectMocks
	private SmtpMailSender smtpMailSender;

	@Test
	void sendEmail() throws MessagingException {
		var request = createValidSendEmailRequest();

		when(mockJavaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

		smtpMailSender.sendEmail(request);

		verify(mockJavaMailSender).send(mockMimeMessage);
		verifyNoMoreInteractions(mockJavaMailSender);
		verify(mockMimeMessage).setFrom(any(String.class));
		verify(mockMimeMessage).setReplyTo(any(Address[].class));
		verify(mockMimeMessage).setRecipients(eq(Message.RecipientType.TO), any(String.class));
		verify(mockMimeMessage).setSubject(any(String.class), any(String.class));
		verify(mockMimeMessage).setContent(any(Multipart.class));
		verify(mockMimeMessage).addHeader("Message-ID", "<318d3a5c-cd45-45ef-94a0-0e3a88e47bf6@sundsvall.se>");
		verify(mockMimeMessage).addHeader("References", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verify(mockMimeMessage).addHeader("In-Reply-To", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verify(mockMimeMessage).addHeader("Auto-Submitted", "auto-generated");
		verifyNoMoreInteractions(mockMimeMessage);
	}

	@Test
	void sendEmail_shouldThrowProblem_whenMessagingException() {
		var request = createValidSendEmailRequest();

		// Fake that the MimeUtility throws an exception when encoding text
		try (MockedStatic<MimeUtility> mimeUtilityMock = mockStatic(MimeUtility.class)) {

			mimeUtilityMock.when(() -> MimeUtility.encodeText(any(String.class), any(String.class), any(String.class)))
				.thenThrow(new UnsupportedEncodingException("Mocked MessagingException"));

			assertThatExceptionOfType(ThrowableProblem.class)
				.isThrownBy(() -> smtpMailSender.sendEmail(request))
				.withMessage("Unable to send e-mail");

			verify(mockJavaMailSender).createMimeMessage();

			verifyNoMoreInteractions(mockJavaMailSender);
			verifyNoInteractions(mockMimeMessage);
		}
	}
}
