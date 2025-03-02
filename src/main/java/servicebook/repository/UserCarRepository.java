package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.UserCar;

public interface UserCarRepository extends JpaRepository<UserCar, Long> {

}
