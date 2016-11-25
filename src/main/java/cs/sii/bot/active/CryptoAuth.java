package cs.sii.bot.active;

import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cs.sii.config.bot.Engine;
import cs.sii.domain.Conversions;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.RWRandom;


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
	private String key;
	private String initVector;
	
	private RWRandom rndIt = new RWRandom();
	private RWRandom rndRnd = new RWRandom();

	
	
	private HashMap<String, Pairs<Long,Integer>> botSeed = new HashMap<>();

	public CryptoAuth() {
		
		seedIterationGenerator1 = "5E1CA498";
		seedIterationGenerator2 = "5fffffffffffffdd";
		seedIterationGenerator3 = "644666D8";
		key = "af6ebe23eacced43"; // 128 bit key
        initVector = "oggifuorepiove17"; // 16 bytes IV

		rndIt.setSeed(Integer.parseInt(seedIterationGenerator1, 16));
		rndRnd.setSeed(Long.parseLong(seedIterationGenerator2, 16));	
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
		return new SecretKeySpec(stringKey.getBytes(), TYPE_MAC);
	}

	/**
	 * valida un hmac generando localmente e verificando con i dati inviati
	 * 
	 * @param hashMac
	 * @return
	 */
	public boolean validateHmac(Long keyNumber, Integer iterationNumber, String hashMac) {
//		Random rnd = new Random();
//		rnd.setSeed(Integer.parseInt(seedIterationGenerator3, 16));
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

	public void addBotChallengeInfo(String idBot,Long key, Integer value) {
		Pairs<Long, Integer> cs=new Pairs<>();
		cs.setValue1(key);
		cs.setValue2(value);
		botSeed.put(idBot,cs);
	}

	public boolean findBotChallengeInfo(String idBot) {
		//IP ip=new IP(ips);
		if (botSeed.get(idBot) != null)
			return true;
		return false;
	}

	public HashMap<String, Pairs<Long, Integer>> getBotSeed() {
		return botSeed;
	}

	
	
	public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
	
	
	
	
	
	
	
	
	
	

}
