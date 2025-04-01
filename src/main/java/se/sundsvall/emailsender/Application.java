package se.sundsvall.emailsender;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.CommandLineRunner;
import se.sundsvall.dept44.ServiceApplication;
import se.sundsvall.emailsender.api.model.SendEmailRequestBuilder;
import se.sundsvall.emailsender.api.model.SenderBuilder;
import se.sundsvall.emailsender.service.MicrosoftGraphMailSender;

@ServiceApplication
public class Application {
	public static void main(String... args) {
		run(Application.class, args);
	}

	// @Bean
	CommandLineRunner foo(final MicrosoftGraphMailSender microsoftGraphMailSender) {
		return args -> {
			var request = SendEmailRequestBuilder.create()
				.withSender(SenderBuilder.create()
					// .withAddress("draken.test.kc@ange.se")
					.withName("ÅngeTest")
					.withAddress("noreply@ange.se")
					.withReplyTo("noreply@ange.se")
					.build())
				.withEmailAddress("lars.oscarson@sundsvall.se")
				.withSubject("Testar")
				.withHtmlMessage("VGVzdGFyIE1TIEdyYXBoLi4u")
				.build();

			microsoftGraphMailSender.sendEmail(request);
		};
	}
}
