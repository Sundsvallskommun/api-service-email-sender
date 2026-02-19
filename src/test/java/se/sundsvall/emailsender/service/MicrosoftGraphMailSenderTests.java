package se.sundsvall.emailsender.service;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import com.microsoft.graph.users.item.UserItemRequestBuilder;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import com.microsoft.graph.users.item.sendmail.SendMailRequestBuilder;
import java.util.AbstractMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.emailsender.api.model.Header;
import se.sundsvall.emailsender.api.model.SendEmailRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.emailsender.TestDataFactory.createValidSendEmailRequest;

@ExtendWith(MockitoExtension.class)
class MicrosoftGraphMailSenderTests {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private GraphServiceClient mockGraphServiceClient;

	private MicrosoftGraphMailSender microsoftGraphMailSender;

	@BeforeEach
	void setUp() {
		microsoftGraphMailSender = new MicrosoftGraphMailSender(mockGraphServiceClient);
	}

	@Test
	void sendEmail() {
		final var request = createValidSendEmailRequest();

		final var microsoftGraphMailSenderSpy = spy(microsoftGraphMailSender);
		final var mockMessage = mock(Message.class);
		when(microsoftGraphMailSenderSpy.createMessage()).thenReturn(mockMessage);
		final var mockSendMailPostRequestBody = mock(SendMailPostRequestBody.class);
		when(microsoftGraphMailSenderSpy.createSendMailPostRequestBody()).thenReturn(mockSendMailPostRequestBody);

		final var mockUsersRequestBuilder = mock(UsersRequestBuilder.class);
		when(mockGraphServiceClient.users()).thenReturn(mockUsersRequestBuilder);
		final var mockUserItemRequestBuilder = mock(UserItemRequestBuilder.class);
		when(mockUsersRequestBuilder.byUserId(request.sender().address())).thenReturn(mockUserItemRequestBuilder);
		final var mockSendMailRequestBuilder = mock(SendMailRequestBuilder.class);
		when(mockUserItemRequestBuilder.sendMail()).thenReturn(mockSendMailRequestBuilder);

		microsoftGraphMailSenderSpy.sendEmail(request);

		verify(microsoftGraphMailSenderSpy).createMessage();
		verify(microsoftGraphMailSenderSpy, times(2)).createRecipient(request.sender().name(), request.sender().address());
		verify(microsoftGraphMailSenderSpy).createRecipient(request.emailAddress());
		verify(microsoftGraphMailSenderSpy).createRecipient(request.sender().replyTo());
		verify(microsoftGraphMailSenderSpy).createRecipient(null, request.emailAddress());
		verify(microsoftGraphMailSenderSpy).createRecipient(null, request.sender().replyTo());
		verify(microsoftGraphMailSenderSpy).createItemBody(request);
		verify(microsoftGraphMailSenderSpy, times(4)).formatHeader(anyList());
		verify(microsoftGraphMailSenderSpy, times(4)).createHeader(any());
		verify(microsoftGraphMailSenderSpy).createAttachment(any());
		verify(microsoftGraphMailSenderSpy).sendEmail(request);

		verify(mockMessage).setFrom(any());
		verify(mockMessage).setSender(any());
		verify(mockMessage).setToRecipients(anyList());
		verify(mockMessage).setReplyTo(anyList());
		verify(mockMessage).setSubject(anyString());
		verify(mockMessage).setBody(any());
		verify(mockMessage).setAttachments(anyList());
		verify(mockMessage).setInternetMessageHeaders(anyList());

		verify(mockSendMailPostRequestBody).setMessage(mockMessage);
		verify(mockSendMailPostRequestBody).setSaveToSentItems(false);

		verify(mockGraphServiceClient).users();
		verify(mockUsersRequestBuilder).byUserId(request.sender().address());
		verify(mockUserItemRequestBuilder).sendMail();
		verify(mockSendMailRequestBuilder).post(mockSendMailPostRequestBody);

		verifyNoMoreInteractions(microsoftGraphMailSenderSpy, mockMessage, mockSendMailPostRequestBody, mockGraphServiceClient, mockUsersRequestBuilder, mockUserItemRequestBuilder, mockSendMailRequestBuilder);
	}

	@Test
	void createItemBodyWhenHtmlMessageIsSet() {
		final var request = SendEmailRequestBuilder.create()
			.withHtmlMessage("c29tZUh0bWxNZXNzYWdl")
			.build();

		final var itemBody = microsoftGraphMailSender.createItemBody(request);

		assertThat(itemBody.getContentType()).isEqualTo(BodyType.Html);
		assertThat(itemBody.getContent()).isEqualTo("someHtmlMessage");
	}

	@Test
	void createItemBodyWhenHtmlMessageIsNotSet() {
		final var request = SendEmailRequestBuilder.create()
			.withMessage("someMessage")
			.build();

		final var itemBody = microsoftGraphMailSender.createItemBody(request);

		assertThat(itemBody.getContentType()).isEqualTo(BodyType.Text);
		assertThat(itemBody.getContent()).isEqualTo("someMessage");
	}

	@Test
	void createRecipientWithEmailAddressOnly() {
		final var emailAddress = "someEmailAddress";

		final var recipient = microsoftGraphMailSender.createRecipient(emailAddress);

		assertThat(recipient.getEmailAddress()).satisfies(recipientEmailAddress -> {
			assertThat(recipientEmailAddress.getAddress()).isEqualTo(emailAddress);
			assertThat(recipientEmailAddress.getName()).isNull();
		});
	}

	@Test
	void createRecipientWithNameAndEmailAddress() {
		final var name = "someName";
		final var emailAddress = "someEmailAddress";

		final var recipient = microsoftGraphMailSender.createRecipient(name, emailAddress);

		assertThat(recipient.getEmailAddress()).satisfies(recipientEmailAddress -> {
			assertThat(recipientEmailAddress.getAddress()).isEqualTo(emailAddress);
			assertThat(recipientEmailAddress.getName()).isEqualTo(name);
		});
	}

	@Test
	void createHeader() {
		final var name = Header.MESSAGE_ID.getKey();
		final var value = "someHeaderValue";
		final var entry = new AbstractMap.SimpleEntry<>(name, List.of(value));

		final var header = microsoftGraphMailSender.createHeader(entry);

		assertThat(header.getName()).isEqualTo("X-" + name);
		assertThat(header.getValue()).isEqualTo(value);
	}
}
