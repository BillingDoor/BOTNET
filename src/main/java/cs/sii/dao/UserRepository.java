package cs.sii.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "user", path = "users")
public interface UserRepository extends CrudRepository<User,String> {

	List<User> findByFirstName(@Param("firstname") String firstname);
	
	User findBySsoId(@Param("ssoId")String ssoId);
}
