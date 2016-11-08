package cs.sii.bot.active;

import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import cs.sii.model.Conversions;

@Service
public class CryptoAuth {

private static final String TYPE_MAC="HmacSHA256";

//TODO da decidere con stringa lunga
//Non necessità di essere cambiata nel tempo
private static final String seedNumberGenerator="";

//TODO da decidere con stringa lunga
private String seedIterationGenerator;

private Random rnd=new Random();	

private HashMap<Long, Long> botSeed=new HashMap<>();

public CryptoAuth(){
	seedIterationGenerator="5ffffffffffffffd";
	rnd.setSeed(Long.parseLong(seedIterationGenerator,16));
}


//TODO creare metodo per la generazione del Seed

/**
 * converte seed in Long
 * @return Long seed
 */
private Long convertSeed(String seed){
	byte[] b =	seed.getBytes();
	return Conversions.bytesToLong(b);
}

/**
 * genera un numero per la creazione del secretKey
 * @param iteration
 */
public Long generateIterationNumber(){
	return rnd.nextLong();
}

/**
 * genera un messaggio randomico utilizzato per la fase di hmac
 */
public Long generateNumberText(){
	 return rnd.nextLong();
}


public String generateStringKey(Long iteration){
	 while(iteration>0){
		rnd.nextLong();
		 iteration--;
	 }
	 return Long.toString(rnd.nextLong());
}


/**
 * genera la secret key da utilizzare per la funziona di Hmac
 */
public SecretKeySpec generateSecretKey(String stringKey){
	return new SecretKeySpec(stringKey.getBytes(),TYPE_MAC);
}

/**
 * valida un hmac generando localmente e verificando con i dati inviati
 * @param hashMac
 * @return
 */
public boolean validateHmac(Long keyNumber,Long iterationNumber,String hashMac){
	String stringKey=generateStringKey(iterationNumber);
	SecretKeySpec secretKey=generateSecretKey(stringKey);	
	if(generateHmac(keyNumber,secretKey).equals(hashMac))
		return true;	
	else
		return false;
}

/**
 * genera la stringa hash hmac
 * @param message
 * @return
 */
public String generateHmac(Long message,SecretKeySpec secretKey){
	String hash="";
	try{
	Mac sha256_HMAC = Mac.getInstance(TYPE_MAC);
	sha256_HMAC.init(secretKey);
	hash = Base64.encodeBase64String(sha256_HMAC.doFinal(Conversions.longToBytes(message)));
}
catch (Exception e){
 System.out.println("Error");
}
return hash;
}


public void addBotChallengeInfo(Long key,Long value){
	botSeed.put(key, value);
}

public boolean findBotChallengeInfo(Long key,Long value){
	if(botSeed.get(key)!=null)
		return true;
		return false;
}

}
