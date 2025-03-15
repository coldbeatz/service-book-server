package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import servicebook.entity.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT COUNT(c) FROM UserCar c WHERE c.car = :car")
    Long hasDependencies(@Param("car") Car car);

    long countCarsByBrandId(Long brandId);

    @Query("SELECT car FROM Car car WHERE car.brand.id = :brandId")
    List<Car> findCarsByBrand(@Param("brandId") Long brandId);
}
