package cs.sii.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotterRepository extends CrudRepository<Botter,Integer> {

}