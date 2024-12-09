package se.sundsvall.emailsender.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

	/**
	 * Default implementation of {@link JavaMailSenderImpl} generates a new message-id before sending the message.
	 * This is an unwanted behaviour, hence we are overriding the updateMessageID to remove the update.
	 */
	static class CustomJavaMailSenderImpl extends JavaMailSenderImpl {

		@Override
		public MimeMessage createMimeMessage() {
			return new CustomMimeMessage(getSession());
		}
	}

	static class CustomMimeMessage extends MimeMessage {

		public CustomMimeMessage(final Session session) {
			super(session);
		}

		@Override
		protected void updateMessageID() {
			// Do nothing, to prevent the message-id from being overwritten
		}
	}
}
