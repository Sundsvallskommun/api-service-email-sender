package se.sundsvall.emailsender.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("junit")
@SpringBootTest(classes = EmailConfiguration.class)
class EmailConfigurationTests {

    @Autowired
    @Qualifier("integration.email.mail-sender")
    private JavaMailSender mailSender;

    @Test
    void mailSenderBean_isNotNull() {
        assertThat(mailSender).isNotNull();
    }
}
