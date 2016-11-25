package cs.sii.service.dao;

import org.springframework.beans.factory.annotation.Autowired;

import cs.sii.model.bot.BotRepository;

public class BotServiceImpl {

	@Autowired
	private BotRepository botRepository;
	
	public BotServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public BotRepository getBotRepository() {
		return botRepository;
	}

	public void setBotRepository(BotRepository botRepository) {
		this.botRepository = botRepository;
	}

	
}
