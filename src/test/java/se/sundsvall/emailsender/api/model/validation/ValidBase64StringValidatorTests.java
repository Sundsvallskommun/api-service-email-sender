package se.sundsvall.emailsender.api.model.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.core.annotation.AnnotationUtils.synthesizeAnnotation;

import java.util.Map;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidBase64StringValidatorTests {

    @Mock
    private ConstraintValidatorContextImpl mockContext;

    private final ValidBase64StringValidator validator = new ValidBase64StringValidator();


    @Test
    void test_usingDefaultValues() {
        var annotation = synthesizeAnnotation(Map.of(), ValidBase64String.class, null);

        validator.initialize(annotation);

        assertThat(validator.isValid(null, mockContext)).isFalse();
        assertThat(validator.isValid("", mockContext)).isFalse();
        assertThat(validator.isValid("string", mockContext)).isTrue();
        assertThat(validator.isValid("<string>", mockContext)).isFalse();

        verify(mockContext, times(3)).addMessageParameter(any(String.class), any());
    }

    @Test
    void test_nullableTrueAndBlankAllowedFalse() {
        var annotation = synthesizeAnnotation(getAnnotationAttributes(true, false), ValidBase64String.class, null);

        validator.initialize(annotation);

        assertThat(validator.isValid(null, mockContext)).isTrue();
        assertThat(validator.isValid("", mockContext)).isFalse();
        assertThat(validator.isValid("string", mockContext)).isTrue();
        assertThat(validator.isValid("<string>", mockContext)).isFalse();

        verify(mockContext, times(2)).addMessageParameter(any(String.class), any());
    }

    @Test
    void test_nullableFalseAndBlankAllowedFalse() {
        var annotation = synthesizeAnnotation(getAnnotationAttributes(false, false), ValidBase64String.class, null);

        validator.initialize(annotation);

        assertThat(validator.isValid(null, mockContext)).isFalse();
        assertThat(validator.isValid("", mockContext)).isFalse();
        assertThat(validator.isValid("string", mockContext)).isTrue();
        assertThat(validator.isValid("<string>", mockContext)).isFalse();

        verify(mockContext, times(3)).addMessageParameter(any(String.class), any());
    }

    @Test
    void test_nullableFalseAndBlankAllowedTrue() {
        var annotation = synthesizeAnnotation(getAnnotationAttributes(false, true), ValidBase64String.class, null);

        validator.initialize(annotation);

        assertThat(validator.isValid(null, mockContext)).isFalse();
        assertThat(validator.isValid("", mockContext)).isTrue();
        assertThat(validator.isValid("string", mockContext)).isTrue();
        assertThat(validator.isValid("<string>", mockContext)).isFalse();

        verify(mockContext, times(2)).addMessageParameter(any(String.class), any());
    }


    @Test
    void test_nullableTrueAndBlankAllowedTrue() {
        var annotation = synthesizeAnnotation(getAnnotationAttributes(true, true), ValidBase64String.class, null);

        validator.initialize(annotation);

        assertThat(validator.isValid(null, mockContext)).isTrue();
        assertThat(validator.isValid("", mockContext)).isTrue();
        assertThat(validator.isValid("string", mockContext)).isTrue();
        assertThat(validator.isValid("<string>", mockContext)).isFalse();

        verify(mockContext, times(1)).addMessageParameter(any(String.class), any());
    }

    private Map<String, Object> getAnnotationAttributes(final boolean nullable, final boolean blankAllowed) {
        return Map.of("nullable", nullable, "blankAllowed", blankAllowed);
    }
}
