package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("junit")
class EmailRequestValidationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidationWithValidRequest() {
        var request = createValidEmailRequest();
        var constraintViolations = validator.validate(request);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithNullSender() {
        var request = createValidEmailRequest(req -> req.setSender(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("sender");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void testValidationWithInvalidSender() {
        var request = createValidEmailRequest(req -> req.getSender().setAddress("not-an-email-address"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("sender.address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");

    }

    @Test
    void testValidationWithNullEmailAddress() {
        var request = createValidEmailRequest(req -> req.setEmailAddress(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankEmailAddress() {
        var request = createValidEmailRequest(req -> req.setEmailAddress(""));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithInvalidEmailAddress() {
        var request = createValidEmailRequest(req -> req.setEmailAddress("kalle"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }
}
