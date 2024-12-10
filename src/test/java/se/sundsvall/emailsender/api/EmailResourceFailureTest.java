package se.sundsvall.emailsender.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;
import static se.sundsvall.emailsender.api.model.Header.MESSAGE_ID;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.emailsender.Application;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class EmailResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";

	private static final String PATH = "/" + MUNICIPALITY_ID + "/send/email";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private EmailService mockService;

	private static Stream<Arguments> invalidRequestsProvider() {
		return Stream.of(
			Arguments.of(createValidEmailRequest(r -> r.setEmailAddress("Not a valid email")), "emailAddress", "must be a well-formed email address"),
			Arguments.of(createValidEmailRequest(r -> r.setSubject("")), "subject", "must not be blank"),
			Arguments.of(createValidEmailRequest(r -> r.setHtmlMessage("Not base64")), "htmlMessage", "not a valid BASE64-encoded string"),
			Arguments.of(createValidEmailRequest(r -> r.setHeaders(Map.of(MESSAGE_ID, List.of("")))), "headers[MESSAGE_ID][0]", "text is not valid message id format"));
	}

	@ParameterizedTest
	@MethodSource("invalidRequestsProvider")
	void sendMailWithInvalidRequestTest(final SendEmailRequest request, final String badArgument, final String expectedMessage) {
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build())
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(tuple(badArgument, expectedMessage));
		});

	}

	@Test
	void sendMailWithInvalidHeadersTest() {
		final var request = createValidEmailRequest(r -> r.setHeaders(Map.of(MESSAGE_ID, List.of("This is invalid"))));

		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build())
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(tuple("headers[MESSAGE_ID][0]", "text is not valid message id format"));
		});

		verifyNoInteractions(mockService);
	}

	@Test
	void sendMailFaultyMunicipalityId() {
		// Arrange
		final var request = createValidEmailRequest();

		// Act
		final var response = webTestClient.post().uri(PATH.replace(MUNICIPALITY_ID, "22-81")).contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(Tuple.tuple("sendMail.municipalityId", "not a valid municipality ID"));
	}

}
