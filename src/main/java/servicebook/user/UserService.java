package servicebook.user;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;

import servicebook.mail.EmailService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void register(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        Context context = new Context();

        context.setVariable("name", "Vlad");
        context.setVariable("date", "07.10.2024");

        try {
            emailService.sendTemplateMessage(user.getEmail(), "Email confirmation", "email-template", context);
        } catch (MessagingException e) {
            e.fillInStackTrace();
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
