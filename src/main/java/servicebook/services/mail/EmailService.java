package servicebook.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import servicebook.user.User;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final EmailConfirmationService emailConfirmationService;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        EmailConfirmationService emailConfirmationService) {

        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailConfirmationService = emailConfirmationService;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendTemplateMessage(String to, String subject, String template, Context context) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String html = templateEngine.process(template, context);

        helper.setTo(to);
        helper.setFrom(fromAddress);
        helper.setText(html, true);
        helper.setSubject(subject);

        mailSender.send(message);
    }

    /**
     * Повідомлення підтвердження реєстрації
     */
    public void sendRegistrationConfirmationEmail(User user) throws MessagingException {
        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user);

        Context context = new Context();

        context.setVariable("name", user.getFullName());
        context.setVariable("link", frontendUrl + "/confirmation/" + emailConfirmation.getUniqueKey());

        String subject = "Email confirmation";
        String template = "email-validation-template";

        sendTemplateMessage(user.getEmail(), subject, template, context);
    }

    /**
     * Повідомлення відновлення доступу до аккаунту
     */
    public void sendRestoreEmail(User user) throws MessagingException {
        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user);

        Context context = new Context();

        context.setVariable("name", user.getFullName());
        context.setVariable("link", frontendUrl + "/restore/" + emailConfirmation.getUniqueKey());

        String subject = "Account recovery";
        String template = "restore-template";

        sendTemplateMessage(user.getEmail(), subject, template, context);
    }
}