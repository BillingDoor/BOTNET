package cs.sii.service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.role.RoleRepository;

@Service
public class RoleServiceImpl {

	@Autowired
	private RoleRepository roleRepository;

	public RoleServiceImpl() {
	}

	/**
	 * @return
	 */
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	public RoleRepository getRoleRepository() {
		return roleRepository;
	}

	public void save(Role role) {
		roleRepository.save(role);
	}

	public void saveAll(List<Role> roles) {
		roleRepository.save(roles);
	}
	public void saveAllObj(List<Object> roles) {
		for (Object obj : roles) {
			roleRepository.save((Role)obj);
		}
		
	}

	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

}
