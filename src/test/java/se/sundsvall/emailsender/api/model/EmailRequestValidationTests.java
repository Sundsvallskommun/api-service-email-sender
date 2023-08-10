package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ActiveProfiles("junit")
class EmailRequestValidationTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void validationWithValidRequest() {
		final var request = createValidEmailRequest();
		final var constraintViolations = validator.validate(request);

		assertThat(constraintViolations).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("testValidationArguments")
	void testValidation(SendEmailRequest request, String constraintField, String constraintMessage) {

		final var constraintViolations = List.copyOf(validator.validate(request));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.get(0).getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.get(0).getMessage()).isEqualTo(constraintMessage);
	}

	private static Stream<Arguments> testValidationArguments() {
		return Stream.of(

			// Validate recipient email address.
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress(null)), "emailAddress", "must not be blank"),
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress("")), "emailAddress", "must not be blank"),
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress("kalle")), "emailAddress", "must be a well-formed email address"),

			// Validate sender email address.
			Arguments.of(createValidEmailRequest(req -> req.getSender().setAddress("not-an-email-address")), "sender.address", "must be a well-formed email address"),
			Arguments.of(createValidEmailRequest(req -> req.setSender(null)), "sender", "must not be null"));
	}
}
