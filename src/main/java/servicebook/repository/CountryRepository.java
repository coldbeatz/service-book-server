package servicebook.repository;

import org.springframework.data.repository.CrudRepository;

import servicebook.entity.Country;

import java.util.Optional;

public interface CountryRepository extends CrudRepository<Country, Long> {

    Optional<Country> findById(long id);
}