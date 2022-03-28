package se.sundsvall.emailsender.api;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Email Resources")
class EmailResource {

    private final EmailService service;

    EmailResource(final EmailService service) {
        this.service = service;
    }

    @Operation(summary = "Send an Email")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successful Operation",
            content = @Content(schema = @Schema(implementation = Void.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = Problem.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = Problem.class))
        )
    })
    @PostMapping("/send/email")
    ResponseEntity<Void> sendMail(@Valid @RequestBody final SendEmailRequest request) throws MessagingException {
        service.sendMail(request);
        return ResponseEntity.ok(null);
    }
}
