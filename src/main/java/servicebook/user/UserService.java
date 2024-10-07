package servicebook.user;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;

import servicebook.mail.EmailService;
import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;
    private final EmailConfirmationService emailConfirmationService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       EmailConfirmationService emailConfirmationService) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.emailConfirmationService = emailConfirmationService;
    }

    public void register(User user) {
        Optional<User> find = userRepository.findByEmail(user.getEmail());

        if (find.isPresent()) {
            User findUser = find.get();

            if (find.get().isConfirmEmail()) {
                throw new IllegalArgumentException("Email is already registered");
            }

            // Якщо користувача не підтверджено - можемо зареєструвати користувача з таким e-mail
            findUser.setFullName(user.getFullName());
            findUser.setPassword(user.getPassword());

            user = findUser;
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        EmailConfirmation emailConfirmation = emailConfirmationService.createEmailConfirmation(user);

        try {
            Context context = new Context();

            context.setVariable("name", user.getFullName());
            context.setVariable("link", "http://localhost:4200/login?key=" + emailConfirmation.getUniqueKey());

            emailService.sendTemplateMessage(user.getEmail(), "Email confirmation", "email-validation-template", context);
        } catch (MessagingException e) {
            e.fillInStackTrace();
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
