package cs.sii.service.crypto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptoPKI {

	private static final String JOLIE_DLL = "jolie.dll";

	private static final String BRAD_DLL = "brad.dll";

	@Autowired
	private CryptoUtils crypto;

	/** Holds the cipher for this object **/
	private Cipher cipher;

	private Signature signature;

	/** Holds the private RSA key of this object **/
	private PrivateKey privRSAKey;
	/** Holds the public RSA key of this object **/
	private PublicKey pubRSAKey;

	private KeyFactory fact;

	/**
	 * Creates a new MsgEncrypt object with no parameters in it
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 */

	public CryptoPKI()
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");

		signature = Signature.getInstance("SHA512WithRSA", "BC");
		fact = KeyFactory.getInstance("RSA", "BC");
	}

	public void generateKeyRSA() {

		// inizializzo generatore chiavi
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA", "BC");
			generator.initialize(4096, new SecureRandom());

			// genero chiavi
			KeyPair keyPair = generator.generateKeyPair();
			// converto le chiavi secondo i propri standard( questo spesso è già
			// in questa forma ma per renderlo cross platform viene impresso
			// nuovamente)
			pubRSAKey = fact.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
			privRSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()));

			// codifico le chiavi secondo lo standard, le encodiamo in base 64,
			// e poi le scriviamo criptate in aes su file
			crypto.encodeObjToFile(BRAD_DLL, Base64.encodeBase64String(pubRSAKey.getEncoded()));
			crypto.encodeObjToFile(JOLIE_DLL, Base64.encodeBase64String(privRSAKey.getEncoded()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | NoSuchPaddingException
				| UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param input
	 * @param receiverPubKey
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public String encryptMessageRSA(String input, PublicKey receiverPubKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

		cipher.init(Cipher.ENCRYPT_MODE, receiverPubKey);
		byte[] inputByte = input.getBytes("utf-8");
		byte[] cipherText = cipher.doFinal(inputByte);
		String encryptedValue = Base64.encodeBase64String(cipherText);
		System.out.println("cipher: " + encryptedValue);
		return encryptedValue;
	}

	/**
	 * @param cipherText
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public String decryptMessageRSA(String cipherText)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		cipher.init(Cipher.DECRYPT_MODE, privRSAKey);
		byte[] decodedValue = Base64.decodeBase64(cipherText);
		byte[] plainText = cipher.doFinal(decodedValue);
		System.out.println("plain : " + new String(plainText));
		return new String(plainText, "utf-8");
	}

	/**
	 * @param message
	 * @return
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public String signMessageRSA(String message) throws SignatureException, InvalidKeyException {
		signature.initSign(privRSAKey);
		byte[] msg = message.getBytes();
		signature.update(msg);
		byte[] sigBytes = signature.sign();
		return Base64.encodeBase64String(sigBytes);
	}

	/**
	 * @param clearMessage
	 * @param cipherMessage
	 * @param senderPubKey
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public Boolean validateSignedMessageRSA(String clearMessage, String cipherMessage, PublicKey senderPubKey)throws InvalidKeyException, SignatureException {
		System.out.println("Testo in chiaro: "+clearMessage+" testo cifrato: "+cipherMessage+" chiave sender: "+senderPubKey);
		byte[] msg = clearMessage.getBytes();
		byte[] sigBytes = Base64.decodeBase64(cipherMessage);
		signature.initVerify(senderPubKey);
		signature.update(msg);
		System.out.println("test: "+sigBytes);
		return signature.verify(sigBytes);
	}

	/**
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * 
	 */
	public boolean loadKeyFromFile() throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException,
			IOException, InvalidKeySpecException {
		// leggo le chiavi da file e le ricostruisco
		byte[] encoded;
		byte[] encoded2;
		encoded = Base64.decodeBase64(crypto.decodeStringFromFile(BRAD_DLL));
		encoded2 = Base64.decodeBase64(crypto.decodeStringFromFile(JOLIE_DLL));

		pubRSAKey = fact.generatePublic(new X509EncodedKeySpec(encoded));
		privRSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(encoded2));
		return true;
	}

	public PublicKey getPubRSAKey() {
		return pubRSAKey;
	}

	public String getPubRSAKeyToString() {
		return demolishPuK(pubRSAKey);
	}

	public PrivateKey getPrivRSAKey() {
		return privRSAKey;
	}

	public void setPrivRSAKey(PrivateKey privRSAKey) {
		this.privRSAKey = privRSAKey;
	}

	public void setPubRSAKey(PublicKey pubRSAkey) {
		this.pubRSAKey = pubRSAkey;
	}

	public void saveToFilePublic() {
	}

	public void readFromFilePublic() {
	}

	public void saveToFilePrivate() {
	}

	public void readFromFilePrivate() {
	}

	/**
	 * @param keyEncoding
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PublicKey rebuildPuK(String keyEncoding) {
		PublicKey puK = null;
		try {
			puK = fact.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(keyEncoding)));
		} catch (InvalidKeySpecException e) {
			System.out.println("errore rigenerazione chiave");
			e.printStackTrace();
		}
		return puK;
	}

	/**
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public String demolishPuK(PublicKey key) {
		return Base64.encodeBase64String(key.getEncoded());
	}

	public CryptoUtils getCrypto() {
		return crypto;
	}

	public void setCrypto(CryptoUtils crypto) {
		this.crypto = crypto;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}
}
