package cs.sii.service.crypto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ActuatorMetricWriter;

import cs.sii.domain.Conversions;
import cs.sii.domain.FileUtil;

public class CryptoUtils {

	
	@Autowired
	private FileUtil fUtils;
	private static final String TYPE_MAC = "HmacSHA256";
	private String key;
	private String initVector;

	public CryptoUtils() {
		key = "af6ebe23eacced43"; // 128 bit key
		initVector = "oggifuorepiove17"; // 16 bytes IV

	}

	public static String getTypeMac() {
		return TYPE_MAC;
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
			System.out.println("hash rifatto " + hash);

		} catch (Exception e) {
			System.out.println("Error");
		}
		return hash;
	}

	public String encryptAES(String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;

	}

	public String decryptAES(String encrypted) {
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


	
public void encodeObjToFile(String filename, ArrayList<Object> data) {
	ArrayList<Object> dataEncrypted= new ArrayList<Object>();
	for(Object obj:data){
		dataEncrypted.add(encryptAES(obj.toString()));
	}
	fUtils.writeObjToFile(filename, dataEncrypted);
}



public ArrayList<String> decodeStringFromFile(String filename){
	ArrayList<String> data= fUtils.readObjFromFile(filename);
	
	ArrayList<String> dataDecrypted= new ArrayList<String>();
	for(String str:data){
		dataDecrypted.add(decryptAES(str));
	}
	return dataDecrypted;
}
	



	
	
}
