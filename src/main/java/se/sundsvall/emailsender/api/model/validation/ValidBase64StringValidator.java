package se.sundsvall.emailsender.api.model.validation;

import java.util.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

public class ValidBase64StringValidator implements ConstraintValidator<ValidBase64String, String> {

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private boolean nullable;
    private boolean blankAllowed;

    @Override
    public void initialize(final ValidBase64String annotation) {
        nullable = annotation.nullable();
        blankAllowed = annotation.blankAllowed();
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext context) {
        if (s == null) {
            if (nullable) {
                return true;
            }

            setMessage(context, "must not be null");

            return false;
        } else if (StringUtils.isEmpty(s)) {
            if (blankAllowed) {
                return true;
            }

            setMessage(context, "must not be null or blank (empty)");

            return false;
        }

        try {
            BASE64_DECODER.decode(s);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage(context, "must be a valid BASE64-encoded string");

            return false;
        }

        return true;
    }

    private void setMessage(final ConstraintValidatorContext context, final String message) {
        ((ConstraintValidatorContextImpl) context).addMessageParameter("message", message);
    }
}
