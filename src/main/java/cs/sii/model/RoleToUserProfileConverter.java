package cs.sii.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import cs.sii.dao.User;
import cs.sii.dao.UserRepository;

/**
 * A converter class used in views to map id's to actual userProfile objects.
 */
@Component
public class RoleToUserProfileConverter implements Converter<Object, User>{

	static final Logger logger = LoggerFactory.getLogger(RoleToUserProfileConverter.class);
	
	@Autowired
	UserRepository userRepository;

	public User convert(Object element) {
		Integer id = Integer.parseInt((String)element);
		User profile= userRepository.findById(id);
		logger.info("Profile : {}",profile);
		return profile;
	}
	
}