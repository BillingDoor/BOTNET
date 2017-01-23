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
	private UserRepository uRep;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * @return
	 */
	public List<User> findAll() {
		return uRep.findAll();

	}

	public void save(User user) {
		User usr = uRep.findBySsoId(user.getSsoId());
		if (usr == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			uRep.save(user);
		}
	}

	public void update(User user) {
		User usr = uRep.findBySsoId(user.getSsoId());
		uRep.delete(usr);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		uRep.save(user);

	}

	public void saveAll(List<User> users) {
		for (User user : users) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			uRep.save(user);
		}
	}

	public void deleteAll() {
		uRep.deleteAll();
	}

	public User findBySsoId(String ssoId) {
		return uRep.findBySsoId(ssoId);
	}

	public User findById(Integer id) {
		return uRep.findById(id);
	}

}
