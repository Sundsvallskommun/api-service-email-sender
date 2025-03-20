package se.sundsvall.emailsender.support;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

public class CustomMimeMessage extends MimeMessage {

	public CustomMimeMessage(final Session session) {
		super(session);
	}

	@Override
	protected void updateMessageID() {
		// Do nothing, to prevent the message-id from being overwritten
	}
}
