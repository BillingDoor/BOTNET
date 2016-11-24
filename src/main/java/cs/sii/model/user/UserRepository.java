package cs.sii.model.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "user", path = "users")
public interface UserRepository extends CrudRepository<User,String> {

	List<User> findByFirstName(@Param("firstname") String firstname);
	
	User findBySsoId(@Param("ssoId")String ssoId);
	
	User findById(@Param("id")Integer id);
}
