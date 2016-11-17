package cs.sii.dao;

import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotterRepository extends CrudRepository<Botter,Integer> {

}