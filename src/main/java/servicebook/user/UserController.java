package servicebook.user;

import jakarta.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.exceptions.ClientException;

import servicebook.services.mail.EmailService;
import servicebook.utils.responce.ErrorResponse;
import servicebook.utils.responce.SuccessResponse;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    private final UserRepository userRepository;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, EmailService emailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
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