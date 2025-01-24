package servicebook.repository;

import org.springframework.data.repository.CrudRepository;

import servicebook.entity.engine.CarEngine;

public interface CarEngineRepository extends CrudRepository<CarEngine, Long> {

}
