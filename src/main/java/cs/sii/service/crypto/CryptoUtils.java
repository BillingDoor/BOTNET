package cs.sii.service.crypto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.domain.Conversions;
import cs.sii.domain.FileUtil;

@Service
public class CryptoUtils {

	@Autowired
	private FileUtil fUtils;
	private static final String TYPE_MAC = "HmacSHA256";
	private static String key = "af6ebe23eacced43";// 128 bit key
	private static String initVector = "oggifuorepiove17"; // 16 bytes IV;

	public CryptoUtils() {

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

	public String generateSha256(String msg) {
		return Base64.encodeBase64String(DigestUtils.sha256(msg));
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
	public String encryptAES(String value)
			throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

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
	public String decryptAES(String encrypted)
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

		return new String(original);
	}

	/**
	 * @param object
	 * @param ostream
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public void encrypt(Serializable object, OutputStream ostream) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		try {
			// Length is 16 byte

			// Create cipher
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			SealedObject sealedObject = new SealedObject(object, cipher);

			// Wrap the output stream
			CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
			ObjectOutputStream outputStream = new ObjectOutputStream(cos);
			outputStream.writeObject(sealedObject);
			outputStream.close();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param istream
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	public  Object decrypt(InputStream istream)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
			ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
			SealedObject sealedObject;

			sealedObject = (SealedObject) inputStream.readObject();
			return sealedObject.getObject(cipher);
		} catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}
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
	 * @throws FileNotFoundException
	 */
	public void encodeObjToFile(String filename, Object data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException {

		fUtils.writeObjToFile(filename, encryptAES(data.toString()));
	}

	/**
	 * @param filename
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws IOException
	 */
	public String decodeStringFromFile(String filename)
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
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
	 * @throws FileNotFoundException
	 */
	public void encodeObjsToFile(String filename, ArrayList<Object> data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException {
		ArrayList<Object> dataEncrypted = new ArrayList<Object>();
		for (Object obj : data) {
			dataEncrypted.add(encryptAES(obj.toString()));
		}
		fUtils.writeObjsToFile(filename, dataEncrypted);
	}

	/**
	 * @param data
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> encodeObjs(ArrayList<Object> data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		ArrayList<String> dataEncrypted = new ArrayList<String>();
		for (Object obj : data) {
			dataEncrypted.add(encryptAES(obj.toString()));
		}
		return dataEncrypted;
	}

	/**
	 * @param filename
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws IOException
	 */
	public ArrayList<Object> decodeStringsToObjs(ArrayList<String> data)
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		ArrayList<Object> dataDecrypted = new ArrayList<Object>();
		for (String str : data) {
			dataDecrypted.add(decryptAES(str));
		}
		return dataDecrypted;
	}

	/**
	 * @param filename
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws IOException
	 */
	public ArrayList<String> decodeStringsFromFile(String filename)
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		ArrayList<String> data = fUtils.readObjsFromFile(filename);

		ArrayList<String> dataDecrypted = new ArrayList<String>();
		for (String str : data) {
			dataDecrypted.add(decryptAES(str));
		}
		return dataDecrypted;
	}

	public String getKey() {
		return key;
	}


	public String getInitVector() {
		return initVector;
	}


}
