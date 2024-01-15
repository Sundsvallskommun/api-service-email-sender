package se.sundsvall.emailsender.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(EmailProperties.class)
class EmailConfiguration {

	private final EmailProperties props;

	EmailConfiguration(final EmailProperties props) {
		this.props = props;
	}

	@Bean("integration.email.mail-sender")
	JavaMailSenderImpl javaMailSender() {
		final var mailSender = new CustomJavaMailSenderImpl();
		mailSender.setHost(props.hostName());
		mailSender.setPort(props.port());

		if (!isBlank(props.username())) {
			mailSender.setUsername(props.username());
		}
		if (!isBlank(props.password())) {
			mailSender.setPassword(props.password());
		}

		// Set additional JavaMail properties
		mailSender.setJavaMailProperties(props.properties());

		return mailSender;
	}

	static class CustomJavaMailSenderImpl extends JavaMailSenderImpl {

		@Override
		public MimeMessage createMimeMessage() {
System.err.println("CREATING CUSTOM MIME MESSAGE");
			return new CustomMimeMessage(getSession());
		}
	}

	static class CustomMimeMessage extends MimeMessage {

		public CustomMimeMessage(final Session session) {
			super(session);
		}

		@Override
		protected void updateMessageID() throws MessagingException {
			// Do nothing, to prevent the message-id from being overwritten
		}
	}
}
