package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import servicebook.entity.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT car FROM Car car WHERE car.brand.id = :brandId")
    List<Car> findCarsByBrand(@Param("brandId") Long brandId);
}
