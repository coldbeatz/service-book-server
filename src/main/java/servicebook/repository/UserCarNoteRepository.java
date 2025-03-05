package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.UserCar;
import servicebook.entity.UserCarNote;

import java.util.List;

public interface UserCarNoteRepository extends JpaRepository<UserCarNote, Long> {

    List<UserCarNote> findByUserCar(UserCar userCar);
}
