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

    EmailConfiguration(EmailProperties props) {
        this.props = props;
    }

    @Bean("integration.email.mailsender")
    JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.getHostName());
        mailSender.setPort(props.getPort());
        if (!StringUtils.isBlank(props.getUsername())) {
            mailSender.setUsername(props.getUsername());
        }
        if (!StringUtils.isBlank(props.getPassword())) {
            mailSender.setPassword(props.getPassword());
        }

        return mailSender;
    }
}
