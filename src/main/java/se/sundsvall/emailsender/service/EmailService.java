package se.sundsvall.emailsender.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.MimeTypeUtils.TEXT_HTML;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;
import jakarta.mail.util.ByteArrayDataSource;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidBase64ConstraintValidator;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

@Service
public class EmailService {

	private static final ValidBase64ConstraintValidator BASE64_VALIDATOR = new ValidBase64ConstraintValidator();

	private final JavaMailSender mailSender;

	public EmailService(@Qualifier("integration.email.mail-sender") final JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMail(final SendEmailRequest request) throws MessagingException {
		final var mimeMessage = createMimeMessage(request);

		mailSender.send(mimeMessage);
	}

	MimeMessage createMimeMessage(final SendEmailRequest request) throws MessagingException {
		final var message = mailSender.createMimeMessage();

		// Handle sender (NAME <ADDRESS>)
		final var sender = new StringBuilder()
			.append(request.getSender().getName())
			.append(" ")
			.append("<").append(request.getSender().getAddress()).append(">");
		message.setFrom(sender.toString());

		// Handle reply-to - if no reply-to address is set, use the sender address
		final var replyTo = Optional.ofNullable(request.getSender().getReplyTo())
			.filter(StringUtils::isNotBlank)
			.orElseGet(() -> request.getSender().getAddress());
		message.setReplyTo(InternetAddress.parse(replyTo));

		// Handle recipient
		message.setRecipients(Message.RecipientType.TO, request.getEmailAddress());
		// Handle subject
		message.setSubject(request.getSubject(), UTF_8.name());
		// Handle content and attachments
		message.setContent(createMultiPart(request));

		return message;
	}

	Multipart createMultiPart(final SendEmailRequest request) throws MessagingException {
		final var multipart = new MimeMultipart("alternative");
		// If plain-text message is provided, add it first, to give priority to HTML if it exists
		if (StringUtils.isNotBlank(request.getMessage())) {
			multipart.addBodyPart((BodyPart) createTextMimePart(request.getMessage()));
		}
		if (StringUtils.isNotBlank(request.getHtmlMessage())) {
			multipart.addBodyPart((BodyPart) createHtmlMimePart(request.getHtmlMessage()));
		}

		// Handle attachments
		for (final var attachment : Optional.ofNullable(request.getAttachments()).orElse(List.of())) {
			if (!BASE64_VALIDATOR.isValid(attachment.getContent())) {
				continue;
			}
			final var content = Base64.getDecoder().decode(attachment.getContent());
			final var attachmentPart = new MimeBodyPart();
			attachmentPart.setFileName(attachment.getName());
			attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(content, attachmentPart.getContentType())));
			attachmentPart.setHeader("Content-Type", attachment.getContentType());
			// Set content-transfer-encoding header to base64, to minimize encoding issues
			attachmentPart.setHeader("Content-Transfer-Encoding", "base64");
			multipart.addBodyPart(attachmentPart);
		}

		return multipart;
	}

	MimePart createTextMimePart(final String content) throws MessagingException {
		final var part = new MimeBodyPart();
		part.setText(content, UTF_8.name(), TEXT_PLAIN.getSubtype());
		return part;
	}

	MimePart createHtmlMimePart(final String content) throws MessagingException {
		return decodeBase64(content)
			.map(throwingFunction(decodedContent -> {
				final var part = new MimeBodyPart();
				part.setText(decodedContent, UTF_8.name(), TEXT_HTML.getSubtype());
				return part;
			}))
			.orElseThrow(() -> new MessagingException("Unable to decode BASE64"));
	}

	Optional<String> decodeBase64(final String s) {
		try {
			return Optional.of(new String(Base64.getDecoder().decode(s), UTF_8));
		} catch (final IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}
