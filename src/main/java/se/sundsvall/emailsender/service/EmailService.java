package se.sundsvall.emailsender.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import se.sundsvall.emailsender.api.domain.EmailRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(@Qualifier("integration.email.mailsender") JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendMail(EmailRequest request) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true, StandardCharsets.UTF_8.name());
        MimeMessage email = createMessage(helper, request);
        mailSender.send(email);

        return true;
    }

    MimeMessage createMessage(MimeMessageHelper helper, EmailRequest request) throws MessagingException {
        helper.setFrom(String.format("%s <%s>", request.getSenderName(), request.getSenderEmail()));
        helper.setTo(request.getEmailAddress());
        helper.setSubject(request.getSubject());

        if (StringUtils.isBlank(request.getHtmlMessage())) {
            helper.setText(request.getMessage());
        } else if (validBase64String(request.getHtmlMessage())) {
            helper.setText(request.getMessage(), new String(Base64.getDecoder().decode(request.getHtmlMessage())));
        }

        for (var attachment : getOrEmptyAttatchment(request.getAttachments())) {
            if (!validBase64String(attachment.getContent())) {
                continue;
            }
            byte[] content = Base64.getDecoder().decode(attachment.getContent());
            helper.addAttachment(attachment.getName(), new ByteArrayResource(content), attachment.getContentType());
        }
        return helper.getMimeMessage();
    }

    Collection<EmailRequest.Attachment> getOrEmptyAttatchment(List<EmailRequest.Attachment> list) {
        return list == null ? Collections.emptyList() : list;
    }

    boolean validBase64String(String content) {
        try {
            Base64.getDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            log.info("faulty base64 ?" + e.getMessage());
            return false;
        }
        return true;
    }
}
