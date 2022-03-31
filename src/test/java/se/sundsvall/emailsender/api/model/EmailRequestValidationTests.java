package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import javax.validation.Validation;
import javax.validation.Validator;

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
        var request = createEmailRequest();
        var constraintViolations = validator.validate(request);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void testValidationWithNullSender() {
        var request = createEmailRequest(req -> req.setSender(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("sender");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void testValidationWithInvalidSender() {
        var request = createEmailRequest(req -> req.getSender().setAddress("not-an-email-address"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("sender.address");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");

    }

    @Test
    void testValidationWithNullEmailAddress() {
        var request = createEmailRequest(req -> req.setEmailAddress(null));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithBlankEmailAddress() {
        var request = createEmailRequest(req -> req.setEmailAddress(""));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testValidationWithInvalidEmailAddress() {
        var request = createEmailRequest(req -> req.setEmailAddress("kalle"));
        var constraintViolations = List.copyOf(validator.validate(request));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("emailAddress");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("must be a well-formed email address");
    }

    private SendEmailRequest createEmailRequest() {
        return createEmailRequest(null);
    }

    private SendEmailRequest createEmailRequest(final Consumer<SendEmailRequest> modifier) {
        var html = Base64.getEncoder().encodeToString("<p>html</p>".getBytes(StandardCharsets.UTF_8));

        var attachment = SendEmailRequest.Attachment.builder()
            .withContent("aGVsbG8gd29ybGQK")
            .withContentType("text/plain")
            .withName("test.txt")
            .build();

        var request = SendEmailRequest.builder()
            .withEmailAddress("some.other.email@someotherhost.com")
            .withSubject("someSubject")
            .withMessage("someMessage")
            .withHtmlMessage(html)
            .withSender(Sender.builder()
                .withName("senderName")
                .withAddress("senderName@somehost.com")
                .withReplyTo("replyTo@somehost.com")
                .build())
            .withAttachments(List.of(attachment, attachment))
            .build();

        if (modifier != null) {
            modifier.accept(request);
        }

        return request;
    }
}
