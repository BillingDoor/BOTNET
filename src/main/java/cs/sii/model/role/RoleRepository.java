package cs.sii.model.role;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cs.sii.model.user.User;

//@RepositoryRestResource(collectionResourceRel = "role", path = "roles")
public interface RoleRepository  extends CrudRepository<Role,Integer> {
	
	Role findById(@Param("id")Integer id);
	
	List<Role> findAll();

	Role findByType(@Param("type")String type);
	
	
}
