package servicebook.user;

import jakarta.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;

import static org.thymeleaf.util.StringUtils.isEmpty;

import servicebook.exceptions.ClientException;

import servicebook.mail.EmailService;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

import java.util.Optional;

@Service
public class UserService {

    @Value("${app.frontend.url}") private String frontendUrl;

    private final UserRepository userRepository;

    private final EmailService emailService;
    private final EmailConfirmationService emailConfirmationService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       EmailConfirmationService emailConfirmationService) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.emailConfirmationService = emailConfirmationService;
    }

    public void register(User user) throws ClientException {
        if (isEmpty(user.getEmail()) || isEmpty(user.getPassword()) || isEmpty(user.getFullName())) {
            throw new ClientException("empty_fields", "Empty fields are not allowed");
        }

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

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        sendConfirmationEmail(user);
    }

    private void sendConfirmationEmail(User user) {
        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user);

        try {
            Context context = new Context();

            context.setVariable("name", user.getFullName());
            context.setVariable("link", frontendUrl + "/confirmation/" + emailConfirmation.getUniqueKey());

            emailService.sendTemplateMessage(user.getEmail(), "Email confirmation", "email-validation-template", context);
        } catch (MessagingException e) {
            e.fillInStackTrace();
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
