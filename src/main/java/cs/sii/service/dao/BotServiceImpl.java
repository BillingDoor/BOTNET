package cs.sii.service.dao;

import java.security.PublicKey;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.domain.IP;
import cs.sii.model.bot.Bot;
import cs.sii.model.bot.BotRepository;

@Service
public class BotServiceImpl {

	@Autowired
	private BotRepository bRep;

	public BotServiceImpl() {
	}

	public Boolean save(Bot bot) {
		System.out.println(" bot "+bot.getIdBot() + " "+ bot.getIp()  + " "+ bot.isElegible()  + " "+ Base64.encodeBase64String(bot.getPubKey().getEncoded()));
		if (bRep.findByIdBot(bot.getIdBot()) == null) {
			bRep.save(bot);
			return true;
		} else
			return false;
	}

	/**
	 * @return
	 */
	public List<Bot> findAll() {
		return bRep.findAll();
	}

	/**
	 * @param id
	 * @return
	 */
	public Bot searchBotId(String id) {
		return bRep.findByIdBot(id);
	}
	
	
	/**
	 * @param ip
	 * @return
	 */
	public Bot searchBotIP(IP ip) {
		return bRep.findByip(ip.toString());
	}

	
	/**
	 * @param ip
	 * @return
	 */
	public Bot searchBotIP(String ip) {
		return bRep.findByip(ip);
	}
	
	
	/**
	 * @param mac
	 * @return
	 */
	public List<Bot> searchBotfindByMac(String mac) {
		return bRep.findByMac(mac);
	}

	/**
	 * @param os
	 * @return
	 */
	public List<Bot> searchBotfindByOs(String os) {
		return bRep.findByOs(os);
	}

	/**
	 * @param name
	 * @return
	 */
	public List<Bot> searchBotfindByUsernameOS(String name) {
		return bRep.findByUsernameOS(name);
	}

	/**
	 * @param key
	 * @return
	 */
	public List<Bot> searchBotfindBypubKey(String key) {
		return bRep.findBypubKey(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public List<Bot> searchBotfindBypubKey(PublicKey key) {

		return bRep.findBypubKey(Base64.encodeBase64String(key.getEncoded()));
	}

	// public BotRepository getBotRepository() {
	// return bRep;
	// }
	//
	// public void setBotRepository(BotRepository botRepository) {
	// this.bRep = botRepository;
	// }

}
