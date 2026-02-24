package se.sundsvall.emailsender.service;

import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.InternetMessageHeader;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.emailsender.api.model.Header;
import se.sundsvall.emailsender.api.model.SendEmailRequest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class MicrosoftGraphMailSender extends AbstractMailSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicrosoftGraphMailSender.class);

	private final GraphServiceClient graphServiceClient;

	public MicrosoftGraphMailSender(final GraphServiceClient graphServiceClient) {
		this.graphServiceClient = graphServiceClient;
	}

	@Override
	public void sendEmail(final SendEmailRequest request) {
		LOGGER.info("Sending email to: {}", request.emailAddress());
		try {
			final var sender = request.sender();

			final var message = createMessage();
			message.setFrom(createRecipient(sender.name(), sender.address()));
			message.setSender(createRecipient(sender.name(), sender.address()));
			message.setSubject(request.subject());
			message.setBody(createItemBody(request));

			// Recipient
			message.setToRecipients(List.of(createRecipient(request.emailAddress())));

			// Reply-to
			final var replyTo = ofNullable(sender.replyTo())
				.filter(StringUtils::isNotBlank)
				.orElse(sender.address());
			message.setReplyTo(List.of(createRecipient(replyTo)));

			// Attachments
			final var attachments = ofNullable(request.attachments()).orElse(emptyList()).stream()
				.map(this::createAttachment)
				.filter(Objects::nonNull)
				.toList();
			if (!attachments.isEmpty()) {
				message.setAttachments(attachments);
			}

			// Headers
			final var headers = ofNullable(request.headers()).orElse(emptyMap()).entrySet().stream()
				.map(this::createHeader)
				.toList();
			if (!headers.isEmpty()) {
				message.setInternetMessageHeaders(headers);
			}

			// Request
			final var requestBody = createSendMailPostRequestBody();
			requestBody.setMessage(message);
			requestBody.setSaveToSentItems(false);

			graphServiceClient.users()
				.byUserId(sender.address())
				.sendMail()
				.post(requestBody);
		} catch (final Exception e) {
			LOGGER.error("Error sending email to: {}", request.emailAddress(), e);
			throw Problem.builder()
				.withStatus(INTERNAL_SERVER_ERROR)
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
		final var itemBody = new ItemBody();

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
		final var address = new EmailAddress();
		address.setAddress(emailAddress);

		// Set the name, if present
		ofNullable(name).ifPresent(address::setName);

		final var recipient = new Recipient();
		recipient.setEmailAddress(address);
		return recipient;
	}

	Attachment createAttachment(final SendEmailRequest.Attachment attachment) {
		if (!BASE64_VALIDATOR.isValid(attachment.content())) {
			return null;
		}
		final var content = Base64.getDecoder().decode(attachment.content());

		final var fileAttachment = new FileAttachment();
		fileAttachment.setName(attachment.name());
		fileAttachment.setContentType(attachment.contentType());
		fileAttachment.setContentBytes(content);
		return fileAttachment;
	}

	InternetMessageHeader createHeader(final Map.Entry<String, List<String>> headerEntry) {
		final var header = new InternetMessageHeader();
		header.setName("X-" + Header.fromString(headerEntry.getKey()).getKey());
		header.setValue(formatHeader(headerEntry.getValue()));
		return header;
	}
}
