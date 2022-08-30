package se.sundsvall.emailsender.api.model.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBase64StringValidator.class)
public @interface ValidBase64String {

    String message() default "{message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

    boolean nullable() default false;

    boolean blankAllowed() default false;
}
