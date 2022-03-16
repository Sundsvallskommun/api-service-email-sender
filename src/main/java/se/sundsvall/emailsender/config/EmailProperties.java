package se.sundsvall.emailsender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.email")
public class EmailProperties {

    private String hostName;
    private int port = 25;
    private String username;
    private String password;
}
