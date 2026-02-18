package se.sundsvall.emailsender.service;

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
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.emailsender.api.model.Header;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.function.Failable.stream;
import static org.springframework.util.MimeTypeUtils.TEXT_HTML;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

public class SmtpMailSender extends AbstractMailSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMailSender.class);

	private final JavaMailSender javaMailSender;

	public SmtpMailSender(final JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void sendEmail(final SendEmailRequest request) {
		try {
			var mimeMessage = createMimeMessage(javaMailSender, request);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			LOGGER.error("Error while sending email to: {}", request.emailAddress(), e);
			throw Problem.builder()
				.withStatus(Status.INTERNAL_SERVER_ERROR)
				.withDetail("Unable to send e-mail")
				.build();
		}
	}

	MimeMessage createMimeMessage(final JavaMailSender mailSender, final SendEmailRequest request) throws MessagingException {
		var message = mailSender.createMimeMessage();

		// Handle sender (NAME <ADDRESS>)
		var sender = request.sender();
		var from = encode(sender.name()) + " " + "<" + sender.address() + ">";
		message.setFrom(from);

		// Handle reply-to - if no reply-to address is set, use the sender address
		var replyTo = ofNullable(sender.replyTo())
			.filter(StringUtils::isNotBlank)
			.orElseGet(sender::address);
		message.setReplyTo(InternetAddress.parse(replyTo));

		// Handle recipient
		message.setRecipients(Message.RecipientType.TO, request.emailAddress());

		// Handle subject
		message.setSubject(request.subject(), UTF_8.name());

		// Handle content and attachments
		message.setContent(createMultiPart(request));

		// Handle optional headers
		stream(ofNullable(request.headers()).orElse(Map.of()).entrySet())
			.forEach(header -> message.addHeader(
				Header.fromString(header.getKey()).getKey(),
				formatHeader(header.getValue())));

		return message;
	}

	String encode(final String s) throws MessagingException {
		try {
			return MimeUtility.encodeText(s, UTF_8.name(), "B");
		} catch (UnsupportedEncodingException e) {
			throw new MessagingException("Encoding error", e);
		}
	}

	Multipart createMultiPart(final SendEmailRequest request) throws MessagingException {
		var multipart = new MimeMultipart("alternative");
		// If plain-text message is provided, add it first, to give priority to HTML if it exists
		if (StringUtils.isNotBlank(request.message())) {
			multipart.addBodyPart((BodyPart) createTextMimePart(request.message()));
		}
		if (StringUtils.isNotBlank(request.htmlMessage())) {
			multipart.addBodyPart((BodyPart) createHtmlMimePart(request.htmlMessage()));
		}

		// Handle attachments
		for (var attachment : ofNullable(request.attachments()).orElse(List.of())) {
			if (!BASE64_VALIDATOR.isValid(attachment.content())) {
				continue;
			}
			var content = Base64.getDecoder().decode(attachment.content());
			var attachmentPart = new MimeBodyPart();
			attachmentPart.setFileName(attachment.name());
			attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(content, attachmentPart.getContentType())));
			attachmentPart.setHeader("Content-Type", attachment.contentType());
			// Set content-transfer-encoding header to base64, to minimize encoding issues
			attachmentPart.setHeader("Content-Transfer-Encoding", "base64");
			multipart.addBodyPart(attachmentPart);
		}

		return multipart;
	}

	MimePart createTextMimePart(final String content) throws MessagingException {
		var part = new MimeBodyPart();
		part.setText(content, UTF_8.name(), TEXT_PLAIN.getSubtype());
		return part;
	}

	MimePart createHtmlMimePart(final String content) throws MessagingException {
		return decodeBase64(content)
			.map(throwingFunction(decodedContent -> {
				var part = new MimeBodyPart();
				part.setText(decodedContent, UTF_8.name(), TEXT_HTML.getSubtype());
				return part;
			}))
			.orElseThrow(() -> new MessagingException("Unable to decode BASE64"));
	}
}
