package servicebook.user.confirmation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.user.User;
import servicebook.user.UserRepository;

import servicebook.user.UserService;
import servicebook.utils.UniqueKeyGenerator;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final UserService userService;

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;

    public Optional<EmailConfirmation> getEmailConfirmation(String key) {
        return emailConfirmationRepository.findByUniqueKey(key);
    }

    @Transactional
    public boolean confirmEmail(Optional<EmailConfirmation> confirmationOptional) {
        if (confirmationOptional.isPresent()) {
            EmailConfirmation confirmation = confirmationOptional.get();
            User user = confirmation.getUser();

            // При зміні пошти через налаштування
            String desiredEmail = confirmation.getDesiredEmail();

            if (!user.getEmail().equals(desiredEmail)) {
                User userByEmail = userService.getUserByEmail(desiredEmail);
                if (userByEmail == null) {
                    user.setEmail(desiredEmail);
                } else {
                    // E-mail адресу вже зайнято
                    emailConfirmationRepository.delete(confirmation);
                    return false;
                }
            }

            user.setEmailConfirmation(null);
            user.setConfirmEmail(true);

            userRepository.save(user);
            emailConfirmationRepository.delete(confirmation);
            return true;
        }

        return false;
    }

    public EmailConfirmation createEmailConfirmation(User user, String desiredEmail) {
        Optional<EmailConfirmation> find = emailConfirmationRepository.findByUser(user);

        EmailConfirmation confirmation;

        if (find.isPresent()) {
            confirmation = find.get();
        } else {
            confirmation = new EmailConfirmation();
            confirmation.setUser(user);
        }

        confirmation.setDesiredEmail(desiredEmail);
        confirmation.setUniqueKey(generateUniqueKey());
        confirmation.setDate(LocalDateTime.now());

        return emailConfirmationRepository.save(confirmation);
    }

    private String generateUniqueKey() {
        String key;

        do {
            key = UniqueKeyGenerator.generateUniqueKey();
        } while (emailConfirmationRepository.findByUniqueKey(key).isPresent());

        return key;
    }
}
