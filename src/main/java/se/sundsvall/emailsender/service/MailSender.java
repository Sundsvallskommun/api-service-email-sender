package se.sundsvall.emailsender.service;

import se.sundsvall.emailsender.api.model.SendEmailRequest;

public interface MailSender {

	void sendEmail(SendEmailRequest request);

	String getMunicipalityId();

	void setMunicipalityId(String municipalityId);
}
