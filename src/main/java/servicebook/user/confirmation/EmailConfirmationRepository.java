package servicebook.user.confirmation;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import servicebook.user.User;

import java.util.Optional;

@Repository
public interface EmailConfirmationRepository extends CrudRepository<EmailConfirmation, Long> {

    Optional<EmailConfirmation> findByUniqueKey(String uniqueKey);
    Optional<EmailConfirmation> findByUser(User user);
}
