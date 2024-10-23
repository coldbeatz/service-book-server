package servicebook.user;

import jakarta.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import static org.thymeleaf.util.StringUtils.isEmpty;

import servicebook.exceptions.ClientException;

import servicebook.mail.EmailService;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void restore(String email) throws ClientException, MessagingException {
        Optional<User> find = userRepository.findByEmail(email);

        if (find.isEmpty()) {
            throw new ClientException("email_not_registered", "Email is invalid");
        }

        User user = find.get();
        emailService.sendRestoreEmail(user);
    }

    public void register(User user) throws ClientException, MessagingException {
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
        emailService.sendRegistrationConfirmationEmail(user);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
