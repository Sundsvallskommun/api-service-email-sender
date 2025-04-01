package se.sundsvall.emailsender.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.emailsender.Application;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class EmailResourceTests {

	private static final String MUNICIPALITY_ID = "2281";

	private static final String PATH = "/" + MUNICIPALITY_ID + "/send/email";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private EmailService mockEmailService;

	@Captor
	private ArgumentCaptor<String> municipalityIdCaptor;
	@Captor
	private ArgumentCaptor<SendEmailRequest> requestCaptor;

	@Test
	void sendMail() throws Exception {
		var request = createValidSendEmailRequest();

		webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		verify(mockEmailService).sendMail(municipalityIdCaptor.capture(), requestCaptor.capture());

		assertThat(municipalityIdCaptor.getValue()).isEqualTo(MUNICIPALITY_ID);
		assertThat(requestCaptor.getValue()).usingRecursiveComparison().isEqualTo(request);
	}
}
