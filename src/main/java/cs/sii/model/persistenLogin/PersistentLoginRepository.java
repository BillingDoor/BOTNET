package cs.sii.model.persistenLogin;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

//@RepositoryRestResource(collectionResourceRel = "persistentlogin", path = "persistentlogin")
public interface PersistentLoginRepository extends CrudRepository<PersistentLogin,String> {
	
	PersistentLogin findFirstByUsername(@Param("username") String username);

}
