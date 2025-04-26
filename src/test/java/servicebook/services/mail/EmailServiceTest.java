package servicebook.services.mail;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    private static final String EMAIL_RECIPIENT = "vbhrytsenko@gmail.com";

    @Test
    void shouldValidateCorrectEmail() {
        assertTrue(emailService.isValid(EMAIL_RECIPIENT));
    }

    @Test
    void shouldInvalidateIncorrectEmail() {
        assertFalse(emailService.isValid("invalid-email"));
    }

    @Test
    void shouldSendRealTextEmail() {
        String subject = "Test Subject";
        String body = "Test Body";

        assertDoesNotThrow(() -> emailService.sendTextMessage(EMAIL_RECIPIENT, subject, body));
    }

    @Test
    void shouldSendRealHtmlEmail() {
        String subject = "Test HTML Subject";
        String htmlBody = "<h1>Test HTML Email</h1>";

        assertDoesNotThrow(() -> emailService.sendHtmlMessage(EMAIL_RECIPIENT, subject, htmlBody));
    }

    @Test
    void shouldSendRegistrationConfirmationEmail() {
        Context context = new Context();

        context.setVariable("name", "username");
        context.setVariable("link", "/confirmation/" + "uniqueKey");

        String subject = "Email confirmation";
        String template = "email-validation-template";

        assertDoesNotThrow(() -> emailService.sendTemplateMessage(EMAIL_RECIPIENT, subject, template, context));
    }

    @Test
    void shouldSendRestoreEmail() {
        Context context = new Context();

        context.setVariable("name", "username");
        context.setVariable("link", "/restore/" + "uniqueKey");

        String subject = "Account recovery";
        String template = "restore-template";

        assertDoesNotThrow(() -> emailService.sendTemplateMessage(EMAIL_RECIPIENT, subject, template, context));
    }
}