package cs.sii.service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cs.sii.model.bot.Bot;
import cs.sii.model.user.User;
import cs.sii.model.user.UserRepository;

@Service
public class UserServiceImpl {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * @return
	 */
	public List<User> findAll() {
		return userRepository.findAll();
	}


	public void save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	public void saveAll(List<User> users) {
		for (User user : users) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
		}
	}
	
	public void deleteAll(){
		userRepository.deleteAll();
	}
	
	
	public User findBySsoId(String ssoId){
		return userRepository.findBySsoId(ssoId);
	}
	
	public User findById(Integer id){
		return userRepository.findById(id);
	}
	
	
//	public void saveAllObj(List<Object> users) {
//		for (Object obj : users) {
//			User user= (User) obj;
//			user.setPassword(passwordEncoder.encode(user.getPassword()));
//			userRepository.save(user);
//		}
//	}

}
