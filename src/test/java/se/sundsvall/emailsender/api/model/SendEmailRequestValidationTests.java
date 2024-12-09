package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidAttachment;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;
import static se.sundsvall.emailsender.TestDataFactory.createValidSender;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SendEmailRequestValidationTests {

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
	@MethodSource("getSendEmailRequestValidationArguments")
	void testSendEmailRequestValidation(final SendEmailRequest request, final String constraintField, final String constraintMessage) {

		final var constraintViolations = List.copyOf(validator.validate(request));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	@ParameterizedTest
	@MethodSource("getSendEmailRequestSenderValidationArguments")
	void testSendEmailRequestSenderValidation(final SendEmailRequest.Sender sender, final String constraintField, final String constraintMessage) {

		final var constraintViolations = List.copyOf(validator.validate(sender));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	@ParameterizedTest
	@MethodSource("getSendEmailRequestAttachmentValidationArguments")
	void testSendEmailRequestAttachmentValidation(final SendEmailRequest.Attachment attachment, final String constraintField, final String constraintMessage) {

		final var constraintViolations = List.copyOf(validator.validate(attachment));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	private static Stream<Arguments> getSendEmailRequestValidationArguments() {
		return Stream.of(

			// Validate recipient email address.
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress(null)), "emailAddress", "must not be blank"),
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress("")), "emailAddress", "must not be blank"),
			Arguments.of(createValidEmailRequest(req -> req.setEmailAddress("kalle")), "emailAddress", "must be a well-formed email address"),

			// Validate sender email address.
			Arguments.of(createValidEmailRequest(req -> req.getSender().setAddress("not-an-email-address")), "sender.address", "must be a well-formed email address"),
			Arguments.of(createValidEmailRequest(req -> req.setSender(null)), "sender", "must not be null"));
	}

	private static Stream<Arguments> getSendEmailRequestSenderValidationArguments() {
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

	private static Stream<Arguments> getSendEmailRequestAttachmentValidationArguments() {
		return Stream.of(
			// Validate attachment content
			Arguments.of(createValidAttachment(a -> a.setContent("Not Valid Base64")), "content", "not a valid BASE64-encoded string"),

			// Validate attachment contentType
			Arguments.of(createValidAttachment(a -> a.setContentType(null)), "contentType", "must not be blank"),
			Arguments.of(createValidAttachment(a -> a.setContentType("")), "contentType", "must not be blank"),

			// Validate attachment name
			Arguments.of(createValidAttachment(a -> a.setName(null)), "name", "must not be blank"),
			Arguments.of(createValidAttachment(a -> a.setName("")), "name", "must not be blank"));

	}
}
