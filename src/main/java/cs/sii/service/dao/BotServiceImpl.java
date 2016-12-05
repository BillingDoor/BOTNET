package cs.sii.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.model.bot.BotRepository;

@Service
public class BotServiceImpl {

	@Autowired
	private BotRepository botRepository;
	
	public BotServiceImpl() {
	}

	public BotRepository getBotRepository() {
		return botRepository;
	}

	public void setBotRepository(BotRepository botRepository) {
		this.botRepository = botRepository;
	}

	
}
