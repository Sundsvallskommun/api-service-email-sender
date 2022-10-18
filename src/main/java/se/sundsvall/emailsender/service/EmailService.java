package se.sundsvall.emailsender.service;

import static org.springframework.util.MimeTypeUtils.TEXT_HTML;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import se.sundsvall.emailsender.api.model.SendEmailRequest;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(@Qualifier("integration.email.mailsender") final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendMail(final SendEmailRequest request) throws MessagingException {
        var mimeMessage = createMimeMessage(request);

        try {
            mailSender.send(mimeMessage);
        } catch (MailSendException e) {
            e.printStackTrace(System.err);
        }

        return true;
    }

    MimeMessage createMimeMessage(final SendEmailRequest request) throws MessagingException {
        var message = mailSender.createMimeMessage();

        // Handle sender (NAME <ADDRESS>)
        var sender = new StringBuilder()
            .append(request.getSender().getName())
            .append(" ")
            .append("<").append(request.getSender().getAddress()).append(">");
        message.setFrom(sender.toString());

        // Handle reply-to - if no reply-to address is set, use the sender address
        var replyTo = Optional.ofNullable(request.getSender().getReplyTo())
            .filter(StringUtils::isNotBlank)
            .orElseGet(() -> request.getSender().getAddress());
        message.setReplyTo(InternetAddress.parse(replyTo));

        // Handle recipient
        message.setRecipients(Message.RecipientType.TO, request.getEmailAddress());
        // Handle subject
        message.setSubject(request.getSubject(), StandardCharsets.UTF_8.name());
        // Handle content and attachments
        message.setContent(createMultiPart(request));
        return message;
    }

    Multipart createMultiPart(final SendEmailRequest request) throws MessagingException {
        var multipart = new MimeMultipart("alternative");
        // If plain-text message is provided, add it first, to give priority to HTML if it exists
        if (StringUtils.isNotBlank(request.getMessage())) {
            multipart.addBodyPart((BodyPart) createTextMimePart(request.getMessage()));
        }
        if (StringUtils.isNotBlank(request.getHtmlMessage())) {
            multipart.addBodyPart((BodyPart) createHtmlMimePart(request.getHtmlMessage()));
        }

        // Handle attachments
        for (var attachment : Optional.ofNullable(request.getAttachments()).orElse(List.of())) {
            if (!isValidBase64(attachment.getContent())) {
                continue;
            }
            byte[] content = Base64.getDecoder().decode(attachment.getContent());
            var attachmentPart  = new MimeBodyPart();
            attachmentPart.setFileName(attachment.getName());
            attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(content, attachmentPart.getContentType())));
            //attachmentPart.setHeader("Content-Type", attachment.getContentType());
            //attachmentPart.setHeader("Content-Transfer-Encoding", "base64");
            multipart.addBodyPart(attachmentPart);
        }

        return multipart;
    }

    MimePart createTextMimePart(final String content) throws MessagingException {
        var part = new MimeBodyPart();
        part.setText(content, StandardCharsets.UTF_8.name(), TEXT_PLAIN.getSubtype());
        return part;
    }

    MimePart createHtmlMimePart(final String content) throws MessagingException {
        return decodeBase64(content)
            .map(throwingFunction(decodedContent -> {
                var part = new MimeBodyPart();
                part.setText(decodedContent, StandardCharsets.UTF_8.name(), TEXT_HTML.getSubtype());
                return part;
            }))
            .orElseThrow(() -> new MessagingException("Unable to decode BASE64"));
    }

    Optional<String> decodeBase64(final String s) {
        try {
            return Optional.of(new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    boolean isValidBase64(String content) {
        try {
            Base64.getDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            LOG.info("Unable to decode BASE64");

            return false;
        }
        return true;
    }
}
