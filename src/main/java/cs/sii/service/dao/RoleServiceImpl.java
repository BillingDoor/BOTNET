package cs.sii.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.model.role.RoleRepository;

@Service
public class RoleServiceImpl{

	@Autowired
	private RoleRepository roleRepository;
	
	public RoleServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public RoleRepository getRoleRepository() {
		return roleRepository;
	}

	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	
	

}
