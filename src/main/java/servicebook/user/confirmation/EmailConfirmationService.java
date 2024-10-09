package servicebook.user.confirmation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.user.User;
import servicebook.user.UserRepository;

import servicebook.utils.UniqueKeyGenerator;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
public class EmailConfirmationService {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmailConfirmationService(EmailConfirmationRepository emailConfirmationRepository,
                                    UserRepository userRepository) {

        this.emailConfirmationRepository = emailConfirmationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean confirmEmail(String key) {
        Optional<EmailConfirmation> find = emailConfirmationRepository.findByUniqueKey(key);

        if (find.isPresent()) {
            EmailConfirmation confirmation = find.get();

            User user = confirmation.getUser();

            user.setEmailConfirmation(null);
            user.setConfirmEmail(true);

            userRepository.save(user);
            emailConfirmationRepository.delete(confirmation);
            return true;
        }

        return false;
    }

    public EmailConfirmation createEmailConfirmation(User user) {
        Optional<EmailConfirmation> find = emailConfirmationRepository.findById(user.getId());

        EmailConfirmation confirmation;

        if (find.isPresent()) {
            confirmation = find.get();
        } else {
            confirmation = new EmailConfirmation();
            confirmation.setUser(user);
        }

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
