package servicebook.user;

import jakarta.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.thymeleaf.util.StringUtils;
import servicebook.exceptions.ClientException;

import servicebook.services.mail.EmailService;

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

    public void register(User user) throws ClientException, MessagingException {
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getFullName())) {
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

    public void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
