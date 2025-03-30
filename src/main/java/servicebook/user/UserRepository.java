package servicebook.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM users u WHERE u.enableEmailNewsletter = true")
    List<String> findAllEmailsWithNewsletterEnabled();
}