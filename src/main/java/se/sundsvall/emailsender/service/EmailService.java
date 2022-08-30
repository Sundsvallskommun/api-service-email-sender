package se.sundsvall.emailsender.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import se.sundsvall.emailsender.api.model.SendEmailRequest;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(@Qualifier("integration.email.mailsender") JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendMail(final SendEmailRequest request) throws MessagingException {
        var helper = new MimeMessageHelper(mailSender.createMimeMessage(), true, StandardCharsets.UTF_8.name());
        var email = createMessage(helper, request);

        mailSender.send(email);

        return true;
    }

    MimeMessage createMessage(final MimeMessageHelper helper, final SendEmailRequest request) throws MessagingException {
        // Handle sender
        var sender = new StringBuilder();
        if (StringUtils.isNotBlank(request.getSender().getName())) {
            sender.append(request.getSender().getName()).append(" ");
        }
        sender.append("<").append(request.getSender().getAddress()).append(">");

        // Handle reply-to: if no reply-to address is set, use the sender address
        var replyTo = request.getSender().getReplyTo();
        helper.setReplyTo(StringUtils.isNotBlank(replyTo) ? replyTo : request.getSender().getAddress());

        helper.setFrom(sender.toString());
        helper.setTo(request.getEmailAddress());
        helper.setSubject(request.getSubject());

        if (StringUtils.isBlank(request.getHtmlMessage())) {
            LOG.info("No HTML message present - sending plain text only");

            helper.setText(request.getMessage());
        } else {
            var decodedHtmlMessage = decodeHtmlMessage(request.getHtmlMessage());

            if (decodedHtmlMessage.isEmpty()) {
                LOG.info("Unable to BASE64-decode HTML message - sending plain text only");

                helper.setText(request.getMessage());
            } else {
                LOG.info("HTML message BASE64-decoded - sending HTML and plain text");

                helper.setText(request.getMessage(), decodedHtmlMessage.get());
            }
        }

        for (var attachment : Optional.ofNullable(request.getAttachments()).orElse(List.of())) {
            if (!isValidBase64String(attachment.getContent())) {
                continue;
            }
            byte[] content = Base64.getDecoder().decode(attachment.getContent());
            helper.addAttachment(attachment.getName(), new ByteArrayResource(content), attachment.getContentType());
        }
        return helper.getMimeMessage();
    }

    Optional<String> decodeHtmlMessage(final String s) {
        try {
            return Optional.of(new String(Base64.getDecoder().decode(s)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    boolean isValidBase64String(String content) {
        try {
            Base64.getDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            LOG.info("Unable to decode BASE64");

            return false;
        }
        return true;
    }
}
