package se.sundsvall.emailsender.support;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * The default implementation of {@link JavaMailSenderImpl} generates a new
 * message-id before sending messages. Since this is an unwanted behaviour,
 * we are overriding the MimeMessage#updateMessageID method to cancel the
 * message-id generation.
 *
 * Also adds a custom municipalityId property to be able to use different SMTP
 * settings for different municipalities.
 */
public class CustomJavaMailSenderImpl extends JavaMailSenderImpl {

	private String municipalityId;

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	@Override
	public MimeMessage createMimeMessage() {
		return new CustomMimeMessage(getSession());
	}
}
