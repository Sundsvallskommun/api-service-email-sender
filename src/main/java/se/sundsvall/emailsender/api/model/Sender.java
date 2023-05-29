package se.sundsvall.emailsender.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sender {

    @NotBlank
    @Schema(description = "The sender of the e-mail")
    private String name;

    @Email
    @NotBlank
    @Schema(description = "Sender e-mail address", example = "sender@sender.se")
    private String address;

    @Email
    @Schema(description = "Reply-to e-mail address", example = "sender@sender.se")
    private String replyTo;
}
