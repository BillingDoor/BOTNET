package cs.sii.service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	public UserRepository getUserRepository() {
		return userRepository;
	}

	/**
	 * @return
	 */
	public List<User> findAll() {
		return userRepository.findAll();
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
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
	public void saveAllObj(List<Object> users) {
		for (Object obj : users) {
			User user= (User) obj;
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
		}
	}

}
