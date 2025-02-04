package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findById(long id);
}