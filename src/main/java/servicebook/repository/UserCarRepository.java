package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.UserCar;
import servicebook.user.User;

import java.util.List;

public interface UserCarRepository extends JpaRepository<UserCar, Long> {

    /**
     * Повертає список автомобілів, що належать вказаному користувачу.
     *
     * @param user Користувач, чиї автомобілі потрібно знайти
     * @return Список автомобілів користувача
     */
    List<UserCar> findAllByUser(User user);
}
