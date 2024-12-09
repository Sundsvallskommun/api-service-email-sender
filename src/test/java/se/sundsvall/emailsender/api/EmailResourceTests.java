package se.sundsvall.emailsender.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
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

	@MockBean
	private EmailService serviceMock;

	@Captor
	private ArgumentCaptor<SendEmailRequest> requestCaptor;

	@Test
	void sendMail() throws Exception {
		// Arrange
		final var request = createValidEmailRequest();

		// Act
		webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		// Assert
		verify(serviceMock).sendMail(requestCaptor.capture());
		assertThat(requestCaptor.getValue()).usingRecursiveComparison().isEqualTo(request);
	}

}
