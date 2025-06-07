package servicebook.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import servicebook.user.User;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final EmailConfirmationService emailConfirmationService;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public boolean isValid(String email) {
        return emailValidator.isValid(email);
    }

    /**
     * Викликати при виникненні помилки при відправці e-mail
     *
     * @param to E-mail отримувача
     * @param e  Помилка
     */
    private void onMessageSendFailed(String to, Throwable e) {
        log.warn("Failed to send email to {}: {}", to, e.getMessage());
    }

    /**
     * Відправка звичайного текстового повідомлення на e-mail
     *
     * @param to      E-mail отримувача, на який буде надіслано повідомлення
     * @param subject Заголовок (тема) повідомлення, який буде відображено у вхідних листах
     * @param body    Вміст повідомлення
     */
    @Async
    public void sendTextMessage(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom(fromAddress);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Sent text email to {}", to);
        } catch (MailException e) {
            onMessageSendFailed(to, e);
        }
    }

    /**
     * Відправка HTML повідомлення на e-mail
     *
     * @param to       E-mail отримувача, на який буде надіслано повідомлення
     * @param subject  Заголовок (тема) повідомлення, який буде відображено у вхідних листах
     * @param htmlBody HTML вміст повідомлення
     */
    @Async
    public void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("Sent html email to {}", to);
        } catch (MessagingException | MailException e) {
            onMessageSendFailed(to, e);
        }
    }

    /**
     * Відправити шаблонне HTML повідомлення на e-mail
     *
     * @param to       E-mail отримувача, на який буде надіслано повідомлення
     * @param subject  Заголовок (тема) повідомлення, який буде відображено у вхідних листах
     * @param template Назва шаблону Thymeleaf, що буде використаний для формування HTML вмісту листа
     * @param context  Контекст із динамічними змінними для заповнення шаблону
     */
    public void sendTemplateMessage(String to, String subject, String template, Context context) {
        String htmlBody = templateEngine.process(template, context);

        sendHtmlMessage(to, subject, htmlBody);
    }

    /**
     * Повідомлення підтвердження реєстрації
     */
    public void sendRegistrationConfirmationEmail(User user, String desiredEmail) {
        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user, desiredEmail);

        Context context = new Context();

        context.setVariable("name", user.getFullName());
        context.setVariable("link", frontendUrl + "/en/confirmation/" + emailConfirmation.getUniqueKey());

        String subject = "Email confirmation";
        String template = "email-validation-template";

        sendTemplateMessage(desiredEmail, subject, template, context);
    }

    public void sendRegistrationConfirmationEmail(User user) {
        sendRegistrationConfirmationEmail(user, user.getEmail());
    }

    /**
     * Повідомлення відновлення доступу до облікового запису
     */
    public void sendRestoreEmail(User user) {
        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user, user.getEmail());

        Context context = new Context();

        context.setVariable("name", user.getFullName());
        context.setVariable("link", frontendUrl + "/en/restore/" + emailConfirmation.getUniqueKey());

        String subject = "Account recovery";
        String template = "restore-template";

        sendTemplateMessage(user.getEmail(), subject, template, context);
    }
}