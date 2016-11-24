package cs.sii.model.bot;

import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotrRepository extends CrudRepository<Bot,Integer> {

}