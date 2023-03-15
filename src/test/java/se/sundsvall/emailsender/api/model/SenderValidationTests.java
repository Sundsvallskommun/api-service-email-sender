package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Consumer;

import javax.validation.Validation;
import javax.validation.Validator;

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
        var sender = createSender();
        var constraintViolations = validator.validate(sender);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithNullName() {
        var sender = createSender(s -> s.setName(null));

        var constraintViolations = List.copyOf(validator.validate(sender));
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("name");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankName() {
        var sender = createSender(s -> s.setName(" "));

        var constraintViolations = List.copyOf(validator.validate(sender));
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("name");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithNullAddress() {
        var request = createSender(s -> s.setAddress(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankAddress() {
        var request = createSender(s -> s.setAddress(""));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithInvalidAddress() {
        var request = createSender(s -> s.setAddress("kalle"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }

    @Test
    void testValidationWithNullReplyTo() {
        var request = createSender(s -> s.setReplyTo(null));

        var constraintViolations = List.copyOf(validator.validate(request));
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithInvalidReplyTo() {
        var request = createSender(s -> s.setReplyTo("not-an-email-address"));

        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("replyTo");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }

    private Sender createSender() {
        return createSender(null);
    }

    private Sender createSender(final Consumer<Sender> modifier) {
        var sender= Sender.builder()
            .withName("Sundsvalls Kommun")
            .withAddress("address@sender.se")
            .withReplyTo("replyto@sender.se")
            .build();

        if (modifier != null) {
            modifier.accept(sender);
        }

        return sender;
    }
}
