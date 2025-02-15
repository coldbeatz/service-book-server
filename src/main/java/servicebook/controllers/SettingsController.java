package servicebook.controllers;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.requests.UserSettingsRequest;

import servicebook.response.SettingsResponse;

import servicebook.services.jwt.JwtService;

import servicebook.services.mail.EmailService;

import servicebook.user.User;
import servicebook.user.UserService;

@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController extends BaseController {

    private final UserService userService;
    private final EmailService emailService;

    private final JwtService jwtService;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserSettingsRequest request) throws MessagingException {
        User user = getAuthenticatedUser();

        // Запит на зміну електронної адреси користувача
        boolean emailConfirmationSent = false;

        String desiredEmail = request.getEmail();
        if (!user.getEmail().equals(desiredEmail) && StringUtils.hasText(desiredEmail)) {
            User userByEmail = userService.getUserByEmail(desiredEmail);

            if (userByEmail != null) {
                return ResponseEntity.badRequest().body("email_already_registered");
            }

            if (!emailValidator.isValid(desiredEmail)) {
                return ResponseEntity.badRequest().body("invalid_email");
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
    private ResponseEntity<?> updatePassword(User user, boolean emailConfirmationSent, UserSettingsRequest request) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        if (!StringUtils.hasText(currentPassword)) {
            return ResponseEntity.badRequest().body("current_password_empty");
        }

        if (!userService.checkUserPassword(user, currentPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("incorrect_old_password");
        }

        userService.setPassword(user, newPassword);
        userService.saveOrUpdate(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new SettingsResponse(emailConfirmationSent, true, token));
    }
}
