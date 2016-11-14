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
import cs.sii.model.IP;
import cs.sii.model.Pairs;
import cs.sii.model.RWRandom;

/**
 * @author chris
 *
 */
@Service
public class CryptoAuth {

	@Autowired
	private Engine eng;
	private static final String TYPE_MAC = "HmacSHA256";

	// TODO da decidere con stringa lunga
	// Non necessità di essere cambiata nel tempo

	// TODO da decidere con stringa lunga
	private String seedIterationGenerator1;
	private String seedIterationGenerator2;
	private String seedIterationGenerator3;

	private RWRandom rndIt = new RWRandom();
	private RWRandom rndRnd = new RWRandom();

	
	
	private HashMap<IP, Pairs<Long,Long>> botSeed = new HashMap<>();

	public CryptoAuth() {
		
		seedIterationGenerator1 = "5ffffffffffffffd";
		seedIterationGenerator2 = "5fffffffffffffdd";
		seedIterationGenerator3 = "5ffffffffffffffe";

		
		if(false){
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
		return rndIt.nextPosLong(Long.MAX_VALUE);
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
	public String generateStringKey(Long iteration) {
		RWRandom rnd = new RWRandom();
		rnd.setSeed(Long.parseLong(seedIterationGenerator3, 16));
		while (iteration > 0) {
			rnd.nextPosLong(Long.MAX_VALUE);
			iteration--;
		}
		return Long.toString(rnd.nextPosLong(Long.MAX_VALUE));
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

	public void addBotChallengeInfo(String ips,Long key, Long value) {
		Pairs<Long, Long> cs=new Pairs<>();
		IP ip=new IP(ips);
		cs.setValue1(key);
		cs.setValue2(value);
		botSeed.put(ip,cs);
	}

	public boolean findBotChallengeInfo(String ips) {
		IP ip=new IP(ips);
		if (botSeed.get(ip) != null)
			return true;
		return false;
	}

	public HashMap<IP, Pairs<Long, Long>> getBotSeed() {
		return botSeed;
	}

	
	

}
