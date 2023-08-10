package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidSender;

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
class SenderValidationTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void testValidationWithValidSender() {
		final var sender = createValidSender();
		final var constraintViolations = validator.validate(sender);

		assertThat(constraintViolations).isEmpty();
	}

	@Test
	void testValidationWithNullReplyTo() {
		final var request = createValidSender(s -> s.setReplyTo(null));

		final var constraintViolations = List.copyOf(validator.validate(request));
		assertThat(constraintViolations).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("testValidationArguments")
	void testValidation(Sender sender, String constraintField, String constraintMessage) {

		final var constraintViolations = List.copyOf(validator.validate(sender));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.get(0).getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.get(0).getMessage()).isEqualTo(constraintMessage);
	}

	private static Stream<Arguments> testValidationArguments() {
		return Stream.of(
			// Validate sender reply-to address
			Arguments.of(createValidSender(s -> s.setReplyTo("not-an-email-address")), "replyTo", "must be a well-formed email address"),

			// Validate sender email address.
			Arguments.of(createValidSender(s -> s.setAddress("kalle")), "address", "must be a well-formed email address"),
			Arguments.of(createValidSender(s -> s.setAddress("")), "address", "must not be blank"),
			Arguments.of(createValidSender(s -> s.setAddress(null)), "address", "must not be blank"),

			// Validate sender name.
			Arguments.of(createValidSender(s -> s.setName(" ")), "name", "must not be blank"),
			Arguments.of(createValidSender(s -> s.setName(null)), "name", "must not be blank"));
	}
}
