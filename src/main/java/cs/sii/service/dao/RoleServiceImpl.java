package cs.sii.service.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.role.RoleRepository;

@Service
public class RoleServiceImpl {

	@Autowired
	private RoleRepository rRep;

	public RoleServiceImpl() {
	}

	/**
	 * @return
	 */
	public List<Role> findAll() {
		return rRep.findAll();
	}

	public Boolean save(Role role) {
		
		if (rRep.findByType(role.getType()) == null) {
			rRep.save(role);
			return true;
		} else
			return false;
	}

	public void saveAll(List<Role> roles) {
		rRep.save(roles);
	}
//	public void saveAllObj(List<Object> roles) {
//		for (Object obj : roles) {
//			System.out.println("class "+ obj.getClass() +obj.toString());
//			System.out.println(((LinkedHashMap<String, String>) obj).size());
//			((LinkedHashMap<String, String>) obj).get("type")
//			System.out.println("gg");
//			//roleRepository.save();
//		}
//	}

	public void setRoleRepository(RoleRepository roleRepository) {
		this.rRep = roleRepository;
	}

	public void deleteAll(){
		rRep.deleteAll();
	}
	
}
