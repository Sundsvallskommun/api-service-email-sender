package se.sundsvall.emailsender.api.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidAttachment;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;
import static se.sundsvall.emailsender.TestDataFactory.createValidSender;

class SendEmailRequestValidationTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void validationWithValidRequest() {
		var request = createValidSendEmailRequest();
		var constraintViolations = validator.validate(request);

		assertThat(constraintViolations).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("getSendEmailRequestValidationArguments")
	void testSendEmailRequestValidation(final SendEmailRequest request, final String constraintField, final String constraintMessage) {
		var constraintViolations = List.copyOf(validator.validate(request));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	@ParameterizedTest
	@MethodSource("getSendEmailRequestSenderValidationArguments")
	void testSendEmailRequestSenderValidation(final SendEmailRequest.Sender sender, final String constraintField, final String constraintMessage) {
		var constraintViolations = List.copyOf(validator.validate(sender));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	@ParameterizedTest
	@MethodSource("getSendEmailRequestAttachmentValidationArguments")
	void testSendEmailRequestAttachmentValidation(final SendEmailRequest.Attachment attachment, final String constraintField, final String constraintMessage) {
		var constraintViolations = List.copyOf(validator.validate(attachment));

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.getFirst().getPropertyPath()).hasToString(constraintField);
		assertThat(constraintViolations.getFirst().getMessage()).isEqualTo(constraintMessage);
	}

	private static Stream<Arguments> getSendEmailRequestValidationArguments() {
		var validEmailRequest = createValidSendEmailRequest();

		return Stream.of(
			// Validate recipient email address.
			Arguments.of(SendEmailRequestBuilder.from(validEmailRequest).withEmailAddress(null).build(), "emailAddress", "must not be blank"),
			Arguments.of(SendEmailRequestBuilder.from(validEmailRequest).withEmailAddress("").build(), "emailAddress", "must not be blank"),
			Arguments.of(SendEmailRequestBuilder.from(validEmailRequest).withEmailAddress("kalle").build(), "emailAddress", "must be a well-formed email address"),

			// Validate sender email address.
			Arguments.of(SendEmailRequestBuilder.from(validEmailRequest)
				.withSender(SenderBuilder.from(validEmailRequest.sender())
					.withAddress("not-an-email-address")
					.build())
				.build(), "sender.address", "must be a well-formed email address"),
			Arguments.of(SendEmailRequestBuilder.from(validEmailRequest).withSender(null).build(), "sender", "must not be null"));
	}

	private static Stream<Arguments> getSendEmailRequestSenderValidationArguments() {
		var validSender = createValidSender();

		return Stream.of(
			// Validate sender reply-to address
			Arguments.of(SenderBuilder.from(validSender).withReplyTo("not-an-email-address").build(), "replyTo", "must be a well-formed email address"),

			// Validate sender email address.
			Arguments.of(SenderBuilder.from(validSender).withAddress("kalle").build(), "address", "must be a well-formed email address"),
			Arguments.of(SenderBuilder.from(validSender).withAddress("").build(), "address", "must not be blank"),
			Arguments.of(SenderBuilder.from(validSender).withAddress(null).build(), "address", "must not be blank"),

			// Validate sender name.
			Arguments.of(SenderBuilder.from(validSender).withName(" ").build(), "name", "must not be blank"),
			Arguments.of(SenderBuilder.from(validSender).withName(null).build(), "name", "must not be blank"));
	}

	private static Stream<Arguments> getSendEmailRequestAttachmentValidationArguments() {
		var validAttachment = createValidAttachment();

		return Stream.of(
			// Validate attachment content
			Arguments.of(AttachmentBuilder.from(validAttachment).withContent("Not Valid Base64").build(), "content", "not a valid BASE64-encoded string"),

			// Validate attachment contentType
			Arguments.of(AttachmentBuilder.from(validAttachment).withContentType(null).build(), "contentType", "must not be blank"),
			Arguments.of(AttachmentBuilder.from(validAttachment).withContentType("").build(), "contentType", "must not be blank"),

			// Validate attachment name
			Arguments.of(AttachmentBuilder.from(validAttachment).withName(null).build(), "name", "must not be blank"),
			Arguments.of(AttachmentBuilder.from(validAttachment).withName("").build(), "name", "must not be blank"));
	}
}
