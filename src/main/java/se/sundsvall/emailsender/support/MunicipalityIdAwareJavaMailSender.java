package se.sundsvall.emailsender.support;

import org.springframework.mail.javamail.JavaMailSender;

public interface MunicipalityIdAwareJavaMailSender extends JavaMailSender {

	String getMunicipalityId();
}
