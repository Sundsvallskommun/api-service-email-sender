package se.sundsvall.emailsender;

import org.springframework.boot.SpringApplication;

import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
public class EmailSenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailSenderApplication.class, args);
    }
}
