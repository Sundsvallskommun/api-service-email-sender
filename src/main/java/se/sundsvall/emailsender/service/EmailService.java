package se.sundsvall.emailsender.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class EmailService {

	private final Map<String, MailSender> mailSenders;

	public EmailService(final List<MailSender> mailSenders) {
		this.mailSenders = mailSenders.stream()
			.collect(toMap(MailSender::getMunicipalityId, Function.identity()));
	}

	public void sendMail(final String municipalityId, final SendEmailRequest request) {
		var mailSender = mailSenders.get(municipalityId);
		if (isNull(mailSender)) {
			throw Problem.valueOf(BAD_GATEWAY, "No mail sender exists for municipalityId " + municipalityId);
		}

		mailSender.sendEmail(request);
	}
}
