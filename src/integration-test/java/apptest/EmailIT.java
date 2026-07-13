package apptest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.emailsender.Application;

@Testcontainers
@ExtendWith(ResourceLoaderExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@WireMockAppTestSuite(files = "classpath:/EmailIT/", classes = Application.class)
class EmailIT extends AbstractAppTest {

	private static final String SERVICE_PATH = "/2281/send/email";
	private static final String REQUEST_FILE = "request.json";

	@Container
	static GenericContainer<?> mailpitContainer = new GenericContainer<>("axllent/mailpit:v1.15")
		.withExposedPorts(1025, 8025)
		.waitingFor(Wait.forLogMessage(".*accessible via.*", 1));

	private final WebTestClient webTestClient;

	EmailIT(@Value("${mailpit.web.port}") final Integer mailpitWebPort) {
		webTestClient = WebTestClient.bindToServer()
			.baseUrl("http://localhost:%d/api/v1".formatted(mailpitWebPort))
			.build();
	}

	@DynamicPropertySource
	static void configureMail(final DynamicPropertyRegistry registry) {
		registry.add("integration.email.instances.2281.basic.host", mailpitContainer::getHost);
		registry.add("integration.email.instances.2281.basic.port", mailpitContainer::getFirstMappedPort);
		registry.add("mailpit.web.port", () -> mailpitContainer.getMappedPort(8025));
	}

	@Test
	void test1_successfulRequest(@Load("/EmailIT/__files/test1_successfulRequest/request.json") final String request) {
		setupCall()
			.withServicePath(SERVICE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Verify what was actually sent
		var jsonPath = JsonPath.parse(request);

		webTestClient.get()
			.uri("/messages")
			.exchange()
			.expectBody()
			.jsonPath("$.total").isEqualTo(1)
			.jsonPath("$.messages[0].MessageID").isEqualTo(jsonPath.read("$.headers.Message-ID[0]", String.class).replace("<", "").replace(">", ""))
			.jsonPath("$.messages[0].Subject").isEqualTo(jsonPath.read("$.subject"))
			.jsonPath("$.messages[0].From.Name").isEqualTo(jsonPath.read("$.sender.name"))
			.jsonPath("$.messages[0].From.Address").isEqualTo(jsonPath.read("$.sender.address"))
			.jsonPath("$.messages[0].To[0].Name").isEmpty()
			.jsonPath("$.messages[0].To[0].Address").isEqualTo(jsonPath.read("$.emailAddress"))
			.jsonPath("$.messages[0].ReplyTo[0].Name").isEmpty()
			.jsonPath("$.messages[0].ReplyTo[0].Address").isEqualTo(jsonPath.read("$.sender.replyTo"))
			.jsonPath("$.messages[0].Attachments").isEqualTo(jsonPath.read("$.attachments.length()"));
	}

	@Test
	void test2_successfulRequestWithRecipientsAndCc(@Load("/EmailIT/__files/test2_successfulRequestWithRecipientsAndCc/request.json") final String request) {
		setupCall()
			.withServicePath(SERVICE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Verify what was actually sent
		var jsonPath = JsonPath.parse(request);

		// Mailpit returns messages newest-first and its state is shared across tests, so assert on the newest ([0])
		webTestClient.get()
			.uri("/messages")
			.exchange()
			.expectBody()
			.jsonPath("$.messages[0].Subject").isEqualTo(jsonPath.read("$.subject"))
			.jsonPath("$.messages[0].To.length()").isEqualTo(2)
			.jsonPath("$.messages[0].To[0].Address").isEqualTo(jsonPath.read("$.recipients[0]"))
			.jsonPath("$.messages[0].To[1].Address").isEqualTo(jsonPath.read("$.recipients[1]"))
			.jsonPath("$.messages[0].Cc.length()").isEqualTo(2)
			.jsonPath("$.messages[0].Cc[0].Address").isEqualTo(jsonPath.read("$.cc[0]"))
			.jsonPath("$.messages[0].Cc[1].Address").isEqualTo(jsonPath.read("$.cc[1]"));
	}
}
