package servicebook.controllers.user;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.controllers.BaseController;
import servicebook.exceptions.BadRequestException;
import servicebook.requests.UserSettingsRequest;

import servicebook.response.SettingsResponse;

import servicebook.services.jwt.JwtService;

import servicebook.services.mail.EmailService;

import servicebook.user.User;
import servicebook.user.UserService;

/**
 * REST-контролер для керування профілем користувача
 */
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class ProfileController extends BaseController {

    private final UserService userService;

    private final EmailService emailService;

    private final JwtService jwtService;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * Оновлення налаштувань користувача: email, ім'я, розсилка, зміна пароля
     *
     * @param request Запит з новими налаштуваннями
     * @return Відповідь про результат оновлення
     * @throws MessagingException якщо виникла помилка при надсиланні листа
     */
    @PutMapping
    public ResponseEntity<SettingsResponse> update(@RequestBody UserSettingsRequest request) throws MessagingException {
        User user = getAuthenticatedUser();

        // Запит на зміну електронної адреси користувача
        boolean emailConfirmationSent = false;

        String desiredEmail = request.getEmail();
        if (!user.getEmail().equals(desiredEmail) && StringUtils.hasText(desiredEmail)) {
            User userByEmail = userService.getUserByEmail(desiredEmail);

            if (userByEmail != null) {
                throw new BadRequestException("email_already_registered");
            }

            if (!emailValidator.isValid(desiredEmail)) {
                throw new BadRequestException("invalid_email");
            }

            emailService.sendRegistrationConfirmationEmail(user, desiredEmail);
            emailConfirmationSent = true;
        }

        boolean userNeedsSave = false;

        // Зміна імені користувача
        String fullName = request.getFullName();

        if (!user.getFullName().equals(fullName)) {
            user.setFullName(fullName);
            userNeedsSave = true;
        }

        // Зміна прапора отримання новин на пошту
        if (request.isEnableEmailNewsletter() != user.isEnableEmailNewsletter()) {
            user.setEnableEmailNewsletter(request.isEnableEmailNewsletter());
            userNeedsSave = true;
        }

        // Можлива зміна пароля
        String newPassword = request.getNewPassword();
        if (StringUtils.hasText(newPassword)) {
            return updatePassword(user, emailConfirmationSent, request);
        }

        if (userNeedsSave) {
            userService.saveOrUpdate(user);
        }

        return ResponseEntity.ok(new SettingsResponse(emailConfirmationSent, userNeedsSave, null));
    }

    /**
     * Обробляє зміну пароля користувача
     *
     * @param user Поточний користувач
     * @param request Запит із новими даними
     *
     * @return ResponseEntity з помилкою або новим токеном
     */
    private ResponseEntity<SettingsResponse> updatePassword(User user, boolean emailConfirmationSent, UserSettingsRequest request) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        if (!StringUtils.hasText(currentPassword)) {
            throw new BadRequestException("current_password_empty");
        }

        if (!userService.checkUserPassword(user, currentPassword)) {
            throw new BadRequestException("incorrect_old_password");
        }

        userService.setPassword(user, newPassword);
        userService.saveOrUpdate(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new SettingsResponse(emailConfirmationSent, true, token));
    }
}
