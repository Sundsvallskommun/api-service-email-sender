package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.emailsender.TestDataFactory.createValidSender;

import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("junit")
class SenderValidationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidationWithValidSender() {
        var sender = createValidSender();
        var constraintViolations = validator.validate(sender);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithNullName() {
        var sender = createValidSender(s -> s.setName(null));

        var constraintViolations = List.copyOf(validator.validate(sender));
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("name");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankName() {
        var sender = createValidSender(s -> s.setName(" "));

        var constraintViolations = List.copyOf(validator.validate(sender));
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("name");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithNullAddress() {
        var request = createValidSender(s -> s.setAddress(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankAddress() {
        var request = createValidSender(s -> s.setAddress(""));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithInvalidAddress() {
        var request = createValidSender(s -> s.setAddress("kalle"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }

    @Test
    void testValidationWithNullReplyTo() {
        var request = createValidSender(s -> s.setReplyTo(null));

        var constraintViolations = List.copyOf(validator.validate(request));
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithInvalidReplyTo() {
        var request = createValidSender(s -> s.setReplyTo("not-an-email-address"));

        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("replyTo");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }
}
