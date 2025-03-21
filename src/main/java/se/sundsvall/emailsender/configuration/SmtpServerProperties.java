package se.sundsvall.emailsender.configuration;

import jakarta.validation.constraints.NotBlank;
import java.util.Properties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
record SmtpServerProperties(

	@NotBlank(message = "must not be blank") String host,
	@DefaultValue("25") Integer port,
	String username,
	String password,
	Properties properties) {}
