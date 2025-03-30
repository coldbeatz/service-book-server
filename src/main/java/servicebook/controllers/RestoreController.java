package servicebook.controllers;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.exceptions.ClientException;

import servicebook.services.mail.EmailService;

import servicebook.requests.restore.RestoreRequest;
import servicebook.requests.restore.RestoreSetPasswordRequest;


import servicebook.user.User;
import servicebook.user.UserRepository;
import servicebook.user.UserService;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationRepository;

import servicebook.utils.responce.ErrorResponse;
import servicebook.utils.responce.SuccessResponse;

import java.util.Optional;

@RestController
@RequestMapping("/restore")
public class RestoreController {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public RestoreController(EmailConfirmationRepository emailConfirmationRepository,
                             UserRepository userRepository,
                             EmailService emailService,
                             UserService userService) {

        this.emailConfirmationRepository = emailConfirmationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    private EmailConfirmation validationKey(String key) throws ClientException {
        Optional<EmailConfirmation> find = emailConfirmationRepository.findByUniqueKey(key);

        if (find.isEmpty()) {
            throw new ClientException("key_is_invalid", "The key won't be found");
        }

        EmailConfirmation confirmation = find.get();
        User user = confirmation.getUser();

        if (!user.isConfirmEmail()) {
            throw new ClientException("email_not_confirmed", "Email not confirmed");
        }
        return find.get();
    }

    @PostMapping(value = "/checkkey")
    public ResponseEntity<?> checkKey(@RequestBody RestoreSetPasswordRequest request) {
        try {
            validationKey(request.getKey());

            return ResponseEntity.ok(new SuccessResponse());
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }

    @PostMapping(value = "/setpassword")
    public ResponseEntity<?> restoreSetPassword(@RequestBody RestoreSetPasswordRequest request) {
        String password = request.getPassword();

        try {
            EmailConfirmation confirmation = validationKey(request.getKey());

            User user = confirmation.getUser();
            user.setEmailConfirmation(null);

            userService.setPassword(user, password);
            userRepository.save(user);

            emailConfirmationRepository.delete(confirmation);

            return ResponseEntity.ok(new SuccessResponse());
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> restore(@RequestBody RestoreRequest request) {
        try {
            Optional<User> find = userRepository.findByEmail(request.getEmail());

            if (find.isEmpty()) {
                throw new ClientException("email_not_registered", "Email is invalid");
            }

            User user = find.get();

            if (!user.isConfirmEmail()) {
                throw new ClientException("email_not_confirmed", "Email not confirmed");
            }

            emailService.sendRestoreEmail(user);

            return ResponseEntity.ok(new SuccessResponse());
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }
}
