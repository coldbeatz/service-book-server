package servicebook.repository;

import org.springframework.data.repository.CrudRepository;

import servicebook.resources.Resource;

public interface ResourceRepository extends CrudRepository<Resource, Long> {

}
