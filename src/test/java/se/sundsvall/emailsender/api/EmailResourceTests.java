package se.sundsvall.emailsender.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.sundsvall.emailsender.TestDataFactory.createValidEmailRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.emailsender.api.model.SendEmailRequest;
import se.sundsvall.emailsender.service.EmailService;

@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class EmailResourceTests {

    @Mock
    private EmailService mockService;

    private EmailResource emailResource;

    @BeforeEach
    public void setUp() {
        emailResource = new EmailResource(mockService);
    }

    @Test
    void sendMail_givenValidDto_shouldReturn_200_OK() throws Exception {
        doNothing().when(mockService).sendMail(any(SendEmailRequest.class));

        var result = emailResource.sendMail(createValidEmailRequest());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(mockService,times(1)).sendMail(any(SendEmailRequest.class));
    }
}
