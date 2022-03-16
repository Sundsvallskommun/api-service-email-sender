package se.sundsvall.emailsender.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("junit")
class EmailRequestTests {

    @Test
    void testGettersAndSetters() {
        var request = new EmailRequest();
        request.setSenderName("someSenderName");
        request.setSenderEmail("someSenderEmail");
        request.setEmailAddress("someEmailAddress");
        request.setSubject("someSubject");
        request.setMessage("someMessage");
        request.setHtmlMessage("someHtmlMessage");
        request.setAttachments(List.of(new EmailRequest.Attachment()));

        assertThat(request.getSenderName()).isEqualTo("someSenderName");
        assertThat(request.getSenderEmail()).isEqualTo("someSenderEmail");
        assertThat(request.getEmailAddress()).isEqualTo("someEmailAddress");
        assertThat(request.getSubject()).isEqualTo("someSubject");
        assertThat(request.getMessage()).isEqualTo("someMessage");
        assertThat(request.getHtmlMessage()).isEqualTo("someHtmlMessage");
        assertThat(request.getAttachments()).hasSize(1);
    }

    @Test
    void testBuilder() {
        var request = EmailRequest.builder()
            .withSenderName("someSenderName")
            .withSenderEmail("someSenderEmail")
            .withEmailAddress("someEmailAddress")
            .withSubject("someSubject")
            .withMessage("someMessage")
            .withHtmlMessage("someHtmlMessage")
            .withAttachments(List.of(
                EmailRequest.Attachment.builder()
                    .withName("someName")
                    .withContent("someContent")
                    .withContentType("someContentType")
                    .build()))
            .build();

        assertThat(request.getSenderName()).isEqualTo("someSenderName");
        assertThat(request.getSenderEmail()).isEqualTo("someSenderEmail");
        assertThat(request.getEmailAddress()).isEqualTo("someEmailAddress");
        assertThat(request.getSubject()).isEqualTo("someSubject");
        assertThat(request.getMessage()).isEqualTo("someMessage");
        assertThat(request.getHtmlMessage()).isEqualTo("someHtmlMessage");
        assertThat(request.getAttachments()).satisfies(attachments -> {
            assertThat(attachments).hasSize(1);
            assertThat(attachments.get(0).getName()).isEqualTo("someName");
            assertThat(attachments.get(0).getContent()).isEqualTo("someContent");
            assertThat(attachments.get(0).getContentType()).isEqualTo("someContentType");
        });
    }
}
