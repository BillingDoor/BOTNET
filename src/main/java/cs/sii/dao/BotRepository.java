package cs.sii.dao;

import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotRepository extends CrudRepository<Bot,Integer> {

}