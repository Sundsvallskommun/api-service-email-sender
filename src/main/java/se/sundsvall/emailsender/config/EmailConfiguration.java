package se.sundsvall.emailsender.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(EmailProperties.class)
class EmailConfiguration {

    private final EmailProperties props;

    EmailConfiguration(final EmailProperties props) {
        this.props = props;
    }

    @Bean("integration.email.mail-sender")
    JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.hostName());
        mailSender.setPort(props.port());

        if (!StringUtils.isBlank(props.username())) {
            mailSender.setUsername(props.username());
        }
        if (!StringUtils.isBlank(props.password())) {
            mailSender.setPassword(props.password());
        }

        // Set additional JavaMail properties
        mailSender.setJavaMailProperties(props.properties());

        return mailSender;
    }
}
