package se.sundsvall.emailsender.api.validation;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.emailsender.api.model.SendEmailRequestBuilder;
import se.sundsvall.emailsender.api.validation.impl.ValidEmailRecipientsConstraintValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;

class ValidEmailRecipientsConstraintValidatorTests {

	private final ValidEmailRecipientsConstraintValidator validator = new ValidEmailRecipientsConstraintValidator();
	private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

	@Test
	void nullRequestIsValid() {
		assertThat(validator.isValid(null, context)).isTrue();
	}

	@Test
	void requestWithEmailAddressIsValid() {
		assertThat(validator.isValid(createValidSendEmailRequest(), context)).isTrue();
	}

	@Test
	void requestWithRecipientsIsValid() {
		var request = SendEmailRequestBuilder.from(createValidSendEmailRequest())
			.withEmailAddress(null)
			.withRecipients(List.of("recipient@recipient.se"))
			.build();

		assertThat(validator.isValid(request, context)).isTrue();
	}

	@Test
	void requestWithoutAnyRecipientIsInvalid() {
		var request = SendEmailRequestBuilder.from(createValidSendEmailRequest())
			.withEmailAddress(null)
			.withRecipients(null)
			.build();

		assertThat(validator.isValid(request, context)).isFalse();
	}
}
