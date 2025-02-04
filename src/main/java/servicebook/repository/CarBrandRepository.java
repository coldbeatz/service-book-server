package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import servicebook.entity.CarBrand;

import java.util.Optional;

public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {

    @Query("SELECT brand FROM CarBrand brand WHERE brand.brand = :brandName")
    Optional<CarBrand> findBrandByName(@Param("brandName") String brandName);
}
