package se.sundsvall.emailsender.config;

import static java.util.Optional.ofNullable;

import java.util.Properties;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.email")
public record EmailProperties(
    String hostName,
    Integer port,
    String username,
    String password,
    Properties properties) {

    @ConstructorBinding
    public EmailProperties(@NotBlank final String hostName, final Integer port, final String username,
            final String password, final Properties properties) {
        this.hostName = hostName;
        this.port = ofNullable(port).orElse(25);
        this.username = username;
        this.password = password;
        this.properties = properties;
    }
}
