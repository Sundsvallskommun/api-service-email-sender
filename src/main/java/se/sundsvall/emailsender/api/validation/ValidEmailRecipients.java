package se.sundsvall.emailsender.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.emailsender.api.validation.impl.ValidEmailRecipientsConstraintValidator;

@Documented
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailRecipientsConstraintValidator.class)
public @interface ValidEmailRecipients {
	String message() default "at least one recipient must be provided in 'emailAddress' or 'recipients'";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
