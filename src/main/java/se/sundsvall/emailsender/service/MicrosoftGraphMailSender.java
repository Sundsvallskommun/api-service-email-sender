package se.sundsvall.emailsender.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.InternetMessageHeader;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

public class MicrosoftGraphMailSender extends AbstractMailSender {

	private final GraphServiceClient graphServiceClient;
	private final String sendAsId;

	public MicrosoftGraphMailSender(final GraphServiceClient graphServiceClient, final String sendAsId) {
		this.graphServiceClient = graphServiceClient;
		this.sendAsId = sendAsId;
	}

	@Override
	public void sendEmail(final SendEmailRequest request) {
		try {
			var sender = request.sender();

			var message = createMessage();
			message.setFrom(createRecipient(sender.name(), sender.address()));
			message.setSender(createRecipient(sender.name(), sender.address()));
			message.setSubject(request.subject());
			message.setBody(createItemBody(request));

			// Recipient
			message.setToRecipients(List.of(createRecipient(request.emailAddress())));

			// Reply-to
			var replyTo = ofNullable(sender.replyTo())
				.filter(StringUtils::isNotBlank)
				.orElse(sender.address());
			message.setReplyTo(List.of(createRecipient(replyTo)));

			// Request
			var requestBody = createSendMailPostRequestBody();
			requestBody.setMessage(message);
			requestBody.setSaveToSentItems(false);

			// Handle optional headers
			var headers = ofNullable(request.headers()).orElse(Map.of()).entrySet().stream()
				.map(header -> createHeader(header.getKey(), formatHeader(header.getValue())))
				.toList();
			message.setInternetMessageHeaders(headers);

			// Send the e-mail
			graphServiceClient.users()
				.byUserId(sendAsId)
				.sendMail()
				.post(requestBody);
		} catch (Exception e) {
			throw Problem.builder()
				.withStatus(Status.INTERNAL_SERVER_ERROR)
				.withDetail("Unable to send e-mail")
				.build();
		}
	}

	Message createMessage() {
		return new Message();
	}

	SendMailPostRequestBody createSendMailPostRequestBody() {
		return new SendMailPostRequestBody();
	}

	ItemBody createItemBody(final SendEmailRequest request) {
		var itemBody = new ItemBody();

		// Prioritize/use HTML, if it's set
		if (isNotBlank(request.htmlMessage())) {
			itemBody.setContentType(BodyType.Html);
			itemBody.setContent(new String(Base64.getDecoder().decode(request.htmlMessage()), UTF_8));
		} else {
			itemBody.setContentType(BodyType.Text);
			itemBody.setContent(request.message());
		}

		return itemBody;
	}

	Recipient createRecipient(final String emailAddress) {
		return createRecipient(null, emailAddress);
	}

	Recipient createRecipient(final String name, final String emailAddress) {
		var address = new EmailAddress();
		address.setAddress(emailAddress);

		// Set the name, if present
		ofNullable(name).ifPresent(address::setName);

		var recipient = new Recipient();
		recipient.setEmailAddress(address);
		return recipient;
	}

	InternetMessageHeader createHeader(final String name, final String value) {
		var header = new InternetMessageHeader();
		header.setName(name);
		header.setValue(value);
		return header;
	}
}
