package servicebook.user;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.*;

import servicebook.controllers.BaseController;

import servicebook.exceptions.ClientException;

import servicebook.localization.Localization;
import servicebook.services.mail.EmailService;

import servicebook.utils.responce.ErrorResponse;
import servicebook.utils.responce.SuccessResponse;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService userService;
    private final EmailService emailService;

    private final UserRepository userRepository;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<User> getAuthUser() {
        User user = getAuthenticatedUser();

        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (!emailValidator.isValid(user.getEmail())) {
                throw new ClientException("invalid_email", "Email is invalid");
            }

            Optional<User> find = userRepository.findByEmail(user.getEmail());

            if (find.isPresent()) {
                User findUser = find.get();

                if (find.get().isConfirmEmail()) {
                    throw new ClientException("email_exists", "Email is already registered");
                }

                // Якщо користувача не підтверджено - можемо зареєструвати користувача з таким e-mail
                findUser.setFullName(user.getFullName());
                findUser.setPassword(user.getPassword());

                user = findUser;
            }

            user.setLocalization(Localization.EN);

            userService.setPassword(user, user.getPassword());

            userRepository.save(user);
            emailService.sendRegistrationConfirmationEmail(user);

            return ResponseEntity.ok(new SuccessResponse());
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("error_send_mail"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }
}