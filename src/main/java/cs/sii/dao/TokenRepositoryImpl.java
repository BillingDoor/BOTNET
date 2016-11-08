package cs.sii.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("tokenRepositoryDao")
@Transactional
public class TokenRepositoryImpl implements PersistentTokenRepository {

	static final Logger logger = LoggerFactory.getLogger(TokenRepositoryImpl.class);

	@Autowired
	private PersistentLoginRepository persistentRepository;
		
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		logger.info("Creating Token for user : {}", token.getUsername());
		PersistentLogin persistentLogin = new PersistentLogin();
		persistentLogin.setUsername(token.getUsername());
		persistentLogin.setSeries(token.getSeries());
		persistentLogin.setToken(token.getTokenValue());
		persistentLogin.setLast_used(token.getDate());
		persistentRepository.save(persistentLogin);
		//persist(persistentLogin);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		logger.info("Fetch Token if any for seriesId : {}", seriesId);
		try {
			//Criteria crit = createEntityCriteria();
			//crit.add(Restrictions.eq("series", seriesId));
			PersistentLogin persistentLogin = persistentRepository.findOne(seriesId);

			return new PersistentRememberMeToken(persistentLogin.getUsername(), persistentLogin.getSeries(),
					persistentLogin.getToken(), persistentLogin.getLast_used());
		} catch (Exception e) {
			logger.info("Token not found...");
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		logger.info("Removing Token if any for user : {}", username);
		//Criteria crit = createEntityCriteria();
		//crit.add(Restrictions.eq("username", username));
		//PersistentLogin persistentLogin = (PersistentLogin) crit.uniqueResult();
		PersistentLogin persistentLogin=persistentRepository.findFirstByUsername(username);
		
		if (persistentLogin != null) {
			logger.info("rememberMe was selected");
			persistentRepository.delete(persistentLogin);
		}

	}

	@Override
	public void updateToken(String seriesId, String tokenValue, Date lastUsed) {
		logger.info("Updating Token for seriesId : {}", seriesId);
		PersistentLogin persistentLogin = persistentRepository.findOne(seriesId);
		persistentLogin.setToken(tokenValue);
		persistentLogin.setLast_used(lastUsed);
		persistentRepository.save(persistentLogin);
		//update(persistentLogin);
	}

}
