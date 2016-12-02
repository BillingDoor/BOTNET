package cs.sii.service.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.domain.Conversions;
import cs.sii.domain.FileUtil;

@Service
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

	/**
	 * @param value
	 * @return
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public String encryptAES(String value) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			// System.out.println("encrypted string: " +
			// Base64.encodeBase64String(encrypted));

			return Base64.encodeBase64String(encrypted);

	}

	/**
	 * @param encrypted
	 * @return
	 * @throws InvalidAlgorithmParameterException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public String decryptAES(String encrypted) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
	}

	/**
	 * @param filename
	 * @param data
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void encodeObjToFile(String filename, Object data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		fUtils.writeObjToFile(filename, encryptAES(data.toString()));
	}

	/**
	 * @param filename
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws InvalidKeyException 
	 */
	public String decodeStringFromFile(String filename) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		String data = fUtils.readObjFromFile(filename);

		return decryptAES(data);
	}

	/**
	 * @param filename
	 * @param data
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void encodeObjsToFile(String filename, ArrayList<Object> data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		ArrayList<Object> dataEncrypted = new ArrayList<Object>();
		for (Object obj : data) {
			dataEncrypted.add(encryptAES(obj.toString()));
		}
		fUtils.writeObjsToFile(filename, dataEncrypted);
	}

	/**
	 * @param filename
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws InvalidKeyException 
	 */
	public ArrayList<String> decodeStringsFromFile(String filename) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		ArrayList<String> data = fUtils.readObjsFromFile(filename);

		ArrayList<String> dataDecrypted = new ArrayList<String>();
		for (String str : data) {
			dataDecrypted.add(decryptAES(str));
		}
		return dataDecrypted;
	}

}
