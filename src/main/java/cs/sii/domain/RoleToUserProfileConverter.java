package cs.sii.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import cs.sii.model.role.Role;
import cs.sii.model.role.RoleRepository;
import cs.sii.model.user.User;
import cs.sii.model.user.UserRepository;

/**
 * A converter class used in views to map id's to actual userProfile objects.
 */
@Component
public class RoleToUserProfileConverter implements Converter<Object, Role>{

	static final Logger logger = LoggerFactory.getLogger(RoleToUserProfileConverter.class);
	
	@Autowired
	RoleRepository userRepository;

	public Role convert(Object element) {
		Integer id = Integer.parseInt((String)element);
		Role profile= userRepository.findById(id);
		logger.info("Profile : {}",profile);
		System.out.println("convert "+profile.toString());
		return profile;
	}
	
}