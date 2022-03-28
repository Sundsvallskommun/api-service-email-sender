package se.sundsvall.emailsender.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import se.sundsvall.emailsender.api.model.SendEmailRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

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
        helper.setFrom(String.format("%s <%s>", request.getSenderName(), request.getSenderEmail()));
        helper.setTo(request.getEmailAddress());
        helper.setSubject(request.getSubject());

        if (StringUtils.isBlank(request.getHtmlMessage())) {
            helper.setText(request.getMessage());
        } else if (isValidBase64String(request.getHtmlMessage())) {
            helper.setText(request.getMessage(), new String(Base64.getDecoder().decode(request.getHtmlMessage())));
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

    boolean isValidBase64String(String content) {
        try {
            Base64.getDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            log.info("faulty base64 ?" + e.getMessage());
            return false;
        }
        return true;
    }
}
