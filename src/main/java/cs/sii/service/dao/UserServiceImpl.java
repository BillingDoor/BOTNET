package cs.sii.service.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cs.sii.model.user.User;
import cs.sii.model.user.UserRepository;

@Service
public class UserServiceImpl {

@Autowired
private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;



public UserRepository getUserRepository() {
	return userRepository;
}



public void setUserRepository(UserRepository userRepository) {
	this.userRepository = userRepository;
}



public void save(User user){
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	userRepository.save(user);
}




}
