package servicebook.controllers.entrance;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.exceptions.BadRequestException;

import servicebook.controllers.exceptions.EmailNotConfirmedException;

import servicebook.response.SuccessResponse;
import servicebook.services.mail.EmailService;

import servicebook.requests.restore.RestoreRequest;
import servicebook.requests.restore.RestoreSetPasswordRequest;


import servicebook.user.User;
import servicebook.user.UserService;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/restore")
public class RestoreController {

    private final EmailService emailService;

    private final UserService userService;

    private final EmailConfirmationService emailConfirmationService;

    /**
     * Запит на відновлення доступу до облікового запису
     */
    @PostMapping
    public ResponseEntity<SuccessResponse> restore(@RequestBody RestoreRequest request) {
        User user = userService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("email_not_registered", "Email is invalid"));

        if (!user.isConfirmEmail()) {
            throw new EmailNotConfirmedException();
        }

        emailService.sendRestoreEmail(user);

        return ResponseEntity.ok().body(new SuccessResponse("restore_email_sent"));
    }

    /**
     * Перевірка ключа для відновлення доступу
     */
    private EmailConfirmation validationKey(String key) {
        Optional<EmailConfirmation> find = emailConfirmationService.findByUniqueKey(key);

        if (find.isEmpty()) {
            throw new BadRequestException("key_is_invalid", "The key won't be found");
        }

        EmailConfirmation confirmation = find.get();
        User user = confirmation.getUser();

        if (!user.isConfirmEmail()) {
            throw new EmailNotConfirmedException();
        }

        return confirmation;
    }

    /**
     * Перевірка ключа
     */
    @PostMapping(value = "/key")
    public ResponseEntity<SuccessResponse> checkKey(@RequestBody RestoreSetPasswordRequest request) {
        validationKey(request.getKey());

        return ResponseEntity.ok().body(new SuccessResponse("key_valid"));
    }

    /**
     * Зміна пароля після перевірки ключа
     */
    @PostMapping(value = "/password")
    public ResponseEntity<SuccessResponse> setPassword(@RequestBody RestoreSetPasswordRequest request) {
        String password = request.getPassword();

        EmailConfirmation confirmation = validationKey(request.getKey());

        User user = confirmation.getUser();
        user.setEmailConfirmation(null);

        userService.setPassword(user, password);
        userService.saveOrUpdate(user);

        emailConfirmationService.delete(confirmation);

        return ResponseEntity.ok().body(new SuccessResponse("password_updated"));
    }
}