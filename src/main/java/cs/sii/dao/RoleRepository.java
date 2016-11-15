package cs.sii.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@RepositoryRestResource(collectionResourceRel = "role", path = "roles")
public interface RoleRepository  extends CrudRepository<Role,Integer> {

}
