package se.sundsvall.emailsender.api.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.api.validation.ValidEmailRecipients;

public class ValidEmailRecipientsConstraintValidator implements ConstraintValidator<ValidEmailRecipients, SendEmailRequest> {

	@Override
	public boolean isValid(final SendEmailRequest request, final ConstraintValidatorContext context) {
		if (request == null) {
			return true;
		}
		return !request.allRecipients().isEmpty();
	}
}
