package se.sundsvall.emailsender.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailResourceTests {

	@Mock
	private EmailService mockService;

	@InjectMocks
	private EmailResource emailResource;

	@Test
	void sendMail_givenValidDto_shouldReturn_200_OK() throws Exception {
		doNothing().when(mockService).sendMail(any(SendEmailRequest.class));

		final var result = emailResource.sendMail(createValidEmailRequest());
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

		verify(mockService).sendMail(any(SendEmailRequest.class));
	}
}
