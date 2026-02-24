package se.sundsvall.emailsender.service;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

	private static final String MUNICIPALITY_ID = "1234";

	@Mock
	private MailSender mockMailSender;

	private EmailService emailService;

	@BeforeEach
	void setUp() {
		when(mockMailSender.getMunicipalityId()).thenReturn(MUNICIPALITY_ID);

		emailService = new EmailService(List.of(mockMailSender));
	}

	@Test
	void sendMailWhenNoMailSenderExistsForMunicipalityId() {
		var municipalityId = "someUnknownMunicipalityId";
		var request = createValidSendEmailRequest();

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> emailService.sendMail(municipalityId, request))
			.satisfies(thrownProblem -> {
				assertThat(thrownProblem.getStatus()).isEqualTo(BAD_GATEWAY);
				assertThat(thrownProblem.getMessage()).endsWith("No mail sender exists for municipalityId " + municipalityId);
			});
	}

	@Test
	void sendMail() {
		var request = createValidSendEmailRequest();

		doNothing().when(mockMailSender).sendEmail(request);

		emailService.sendMail(MUNICIPALITY_ID, request);

		verify(mockMailSender).sendEmail(request);
	}
}
