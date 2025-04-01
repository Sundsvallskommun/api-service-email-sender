package se.sundsvall.emailsender.api;

import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@RestController
@Tag(name = "Email resources", description = "Resources for sending emails")
@Validated
class EmailResource {

	private final EmailService service;

	EmailResource(final EmailService service) {
		this.service = service;
	}

	@PostMapping("{municipalityId}/send/email")
	@Operation(summary = "Send an e-mail", responses = {
		@ApiResponse(
			responseCode = "200",
			description = "Successful Operation",
			useReturnTypeSchema = true),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request",
			content = @Content(schema = @Schema(oneOf = {
				Problem.class, ConstraintViolationProblem.class
			}))),
		@ApiResponse(
			responseCode = "500",
			description = "Internal Server Error",
			content = @Content(schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> sendMail(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Valid @RequestBody final SendEmailRequest request) {

		service.sendMail(municipalityId, request);

		return ok().build();
	}
}
