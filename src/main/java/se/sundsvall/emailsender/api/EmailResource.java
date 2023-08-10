package se.sundsvall.emailsender.api;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@RestController
class EmailResource {

	private final EmailService service;

	EmailResource(final EmailService service) {
		this.service = service;
	}

	@Operation(summary = "Send an e-mail")
	@ApiResponse(
		responseCode = "200",
		description = "Successful Operation")
	@ApiResponse(
		responseCode = "400",
		description = "Bad Request",
		content = @Content(schema = @Schema(implementation = Problem.class)))
	@ApiResponse(
		responseCode = "500",
		description = "Internal Server Error",
		content = @Content(schema = @Schema(implementation = Problem.class)))
	@PostMapping("/send/email")
	ResponseEntity<Void> sendMail(@Valid @RequestBody final SendEmailRequest request) throws MessagingException {
		service.sendMail(request);

		return ok().build();
	}
}
