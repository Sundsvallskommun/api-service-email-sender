package se.sundsvall.emailsender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import java.util.List;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

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

		verify(mockMailSender).send(mockMimeMessage);
		verifyNoMoreInteractions(mockMailSender);
		verify(mockMimeMessage).setFrom(any(String.class));
		verify(mockMimeMessage).setReplyTo(any(Address[].class));
		verify(mockMimeMessage).setRecipients(eq(Message.RecipientType.TO), any(String.class));
		verify(mockMimeMessage).setSubject(any(String.class), any(String.class));
		verify(mockMimeMessage).setContent(any(Multipart.class));
		verify(mockMimeMessage).setHeader("Message-ID", "<318d3a5c-cd45-45ef-94a0-0e3a88e47bf6@sundsvall.se>");
		verify(mockMimeMessage).setHeader("References", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verify(mockMimeMessage).setHeader("In-Reply-To", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verifyNoMoreInteractions(mockMimeMessage);
	}

	@Test
	void formatHeaderTest() {
		List<String> strings = List.of("abc@abc", "bac@bac", "cab@cab");

		var result = service.formatHeader(strings);

		assertThat(result).isEqualTo("abc@abc\r\nbac@bac\r\ncab@cab");
	}

	@Test
	void applyCustomHeadersTest() throws Exception {
		var request = createValidEmailRequest();

		service.addOptionalHeaders(mockMimeMessage, request);

		verify(mockMimeMessage).setHeader("Message-ID", "<318d3a5c-cd45-45ef-94a0-0e3a88e47bf6@sundsvall.se>");
		verify(mockMimeMessage).setHeader("References", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verify(mockMimeMessage).setHeader("In-Reply-To", "<5e0b2ce9-9b0c-4f8b-aa62-ebac666c5b64@sundsvall.se>");
		verify(mockMimeMessage, times(3)).setHeader(any(String.class), any(String.class));
		verifyNoMoreInteractions(mockMimeMessage);
	}

	@Test
	void applyCustomHeaders_nullHeadersTest() throws Exception {
		var request = createValidEmailRequest(r -> r.setHeaders(null));

		service.addOptionalHeaders(mockMimeMessage, request);

		verify(mockMimeMessage, never()).setHeader(any(String.class), any(String.class));
		verifyNoMoreInteractions(mockMimeMessage);
	}
}
