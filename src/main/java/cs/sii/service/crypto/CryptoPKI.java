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

/**
 * @author Loopo
 *
 */
@Service
public class CryptoPKI {

	private static final String JOLIE_DLL = "jolie.dll";

	private static final String BRAD_DLL = "brad.dll";

	@Autowired
	private CryptoUtils crypto;

	/** Holds the cipher for this object **/
	private Cipher cipher;

	private Signature signature;

	// /** Holds the private RSA key of this object **/
	// private PrivateKey privRSAKey;
	// /** Holds the public RSA key of this object **/
	// private PublicKey pubRSAKey;

	/** Holds the private RSA key of this object **/
	private PrivateKey privERSAKey;
	/** Holds the public RSA key of this object **/
	private PublicKey pubERSAKey;

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
		// cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
		// cipher = Cipher.getInstance("RSA/None/PKCS1Padding","BC");

		signature = Signature.getInstance("SHA512WithRSA", "BC");
		fact = KeyFactory.getInstance("RSA", "BC");
	}

	public void generateKeyRSA() {
		// byte[] input = "aa".getBytes();

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
			pubERSAKey = fact.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
			privERSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()));

			// codifico le chiavi secondo lo standard, le encodiamo in base 64,
			// e poi le scriviamo criptate in aes su file
			crypto.encodeObjToFile(BRAD_DLL, Base64.encodeBase64String(pubERSAKey.getEncoded()));
			crypto.encodeObjToFile(JOLIE_DLL, Base64.encodeBase64String(privERSAKey.getEncoded()));
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
		cipher.init(Cipher.DECRYPT_MODE, privERSAKey);
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
		signature.initSign(privERSAKey);
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
	public Boolean validateSignedMessageRSA(String clearMessage, String cipherMessage, PublicKey senderPubKey)
			throws InvalidKeyException, SignatureException {
		byte[] msg = clearMessage.getBytes();
		byte[] sigBytes = Base64.decodeBase64(cipherMessage);
		signature.initVerify(senderPubKey);
		signature.update(msg);
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

		pubERSAKey = fact.generatePublic(new X509EncodedKeySpec(encoded));
		privERSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(encoded2));
		return true;
	}

	public PublicKey getPubRSAKey() {
		return pubERSAKey;
	}
	public String getPubRSAKeyToString() {
		return demolishPuK(pubERSAKey);
	}
	public PrivateKey getPrivRSAKey() {
		return privERSAKey;
	}

	public void setPrivRSAKey(PrivateKey privRSAKey) {
		this.privERSAKey = privRSAKey;
	}

	public void setPubRSAKey(PublicKey pubRSAkey) {
		this.pubERSAKey = pubRSAkey;
	}

	public void saveToFilePublic() {
	}

	public void readFromFilePublic() {
	}

	public void saveToFilePrivate() {
	}

	public void readFromFilePrivate() {
	}

//	 private PrivateKey rebuildPrK(String keyEncoding) throws Exception {
//	 byte[] encoded = Base64.decodeBase64(keyEncoding);
//	 PrivateKey prK = KeyFactory.getInstance("RSA").generatePrivate(new
//	 PKCS8EncodedKeySpec(encoded));
//	 return prK;
//	 }
	//
	 /**
	 * @param keyEncoding
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PublicKey rebuildPuK(String keyEncoding)  {
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
	public String demolishPuK(PublicKey key)  {
	 return  Base64.encodeBase64String(key.getEncoded());
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

	public PrivateKey getPrivERSAKey() {
		return privERSAKey;
	}

	public void setPrivERSAKey(PrivateKey privERSAKey) {
		this.privERSAKey = privERSAKey;
	}

	public PublicKey getPubERSAKey() {
		return pubERSAKey;
	}

	public void setPubERSAKey(PublicKey pubERSAKey) {
		this.pubERSAKey = pubERSAKey;
	}

	//
	// /**
	// * Sets the public parameters to the ones given in string
	// * parameter
	// *
	// * @param params must be of the form g p l, where g, p, and l
	// * are the public parameters for DH key exchange
	// */
	// public void setPubParams(String params) {
	// String[] parts = params.split(" ");
	// BigInteger g = new BigInteger(parts[0]);
	// BigInteger p = new BigInteger(parts[1]);
	// int l = Integer.parseInt(parts[2]);
	// KeyPair keyPair = getKeyPair(g, p, l);
	// this.pubKey = keyPair.getPublic();
	// this.privKey = keyPair.getPrivate();
	// ByteArrayOutputStream baos = null;
	// ObjectOutputStream oos = null;
	// try {
	// baos = new ByteArrayOutputStream();
	// oos = new ObjectOutputStream(baos);
	// oos.writeObject(pubKey);
	// byte[] pubKeyBytes = baos.toByteArray();
	// this.strPubKey = new Base64().encodeToString(pubKeyBytes);
	// oos.close();
	// baos.close();
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Not persisted");
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * Gets the public information for a DH key exchange or null if the
	// parameters
	// * could not be made
	// *
	// * @return a PubInfo object that holds the public information
	// * for a DH key exchange or null if the parameters could not
	// * be made
	// */
	// public static PubInfo getPubParams() {
	// try {
	// AlgorithmParameterGenerator paramGen =
	// AlgorithmParameterGenerator.getInstance("DH");
	// paramGen.init(1024);
	// AlgorithmParameters params = paramGen.generateParameters();
	//
	// DHParameterSpec dhSpec =
	// (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
	//
	// BigInteger g = dhSpec.getG();
	// BigInteger p = dhSpec.getP();
	// int l = dhSpec.getL();
	//
	// return new PubInfo(g, p, l);
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Could not make public parameters");
	// }
	// return null;
	// }
	// }
	//
	// /**
	// * Gets the KeyPair that will be used by this object and that uses
	// * the given parameters for DH key exchange or null if the pair could
	// * not be created
	// *
	// * @param g != null
	// * @param p != null
	// * @return the KeyPair that will be used by this object or null if the
	// * pair could not be created
	// */
	// private KeyPair getKeyPair(BigInteger g, BigInteger p, int l) {
	// try {
	// DHParameterSpec dhSpec = new DHParameterSpec(p, g, l);
	//
	// KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
	//
	// keyGen.initialize(dhSpec);
	//
	// KeyPair keyPair = keyGen.generateKeyPair();
	// return keyPair;
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Could not create KeyPair");
	// }
	// return null;
	// }
	// }
	//
	// /**
	// * Returns a serialized string version of the public
	// * key for this object
	// *
	// * @return a serialized string version of the public key
	// */
	// public String getStrKey() {
	// return strPubKey;
	// }
	//
	// /**
	// * Performs a handshake for key exchange between two people. This method
	// * must be called by both people to ensure that the agreement has been
	// * reached on both ends.
	// *
	// * @param otherKey the serialized string version of the public key for
	// * the other person. Must not be null.
	// */
	// public void handShake(String otherKey) {
	// if (DEBUG) {
	// System.out.println("Performing handshake...");
	// }
	// try {
	// byte[] otherPubBytes = new Base64().decode(otherKey);
	// ByteArrayInputStream bais = new ByteArrayInputStream(otherPubBytes);
	// ObjectInputStream ois = new ObjectInputStream(bais);
	//
	// KeyAgreement keyAgree = KeyAgreement.getInstance("DiffieHellman");
	// keyAgree.init(privKey);
	// Key otherPub = (Key) ois.readObject();
	// keyAgree.doPhase(otherPub, true);
	// msgKey = keyAgree.generateSecret("DESede");
	// cipher = Cipher.getInstance("DESede");
	// mac = Mac.getInstance("HmacSHA512");
	// if (DEBUG) {
	// System.out.println("Handshake completed");
	// }
	// } catch (Exception e) {
	// System.out.println("Could not complete handshake...");
	// System.out.println("Agreement not confirmed");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * Returns a random nonce to be used in encryption and decryption
	// * or -1 if a nonce could not be generated
	// *
	// * @return a random nonce or -1 if a nonce could not be generated
	// */
	// public int getNonce() {
	// try {
	// Random ranGen = SecureRandom.getInstance("SHA1PRNG");
	// int nonce = ranGen.nextInt();
	// while (nonceSet.contains(nonce)) {
	// nonce = ranGen.nextInt();
	// }
	// nonceSet.add(nonce);
	// return nonce;
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Nonce could not be generated");
	// }
	// return getNonce();
	// }
	// }
	//
	// /**
	// * Encrypts the given msg and returns the ciphertext for the
	// * encryted message
	// *
	// * @param msg != null
	// * @return the encrypted message or null if encryption fails
	// */
	// public String encryptMsg(String msg) {
	// try {
	// cipher.init(Cipher.ENCRYPT_MODE, msgKey);
	// mac.init(msgKey);
	// byte[] c1 = cipher.doFinal(msg.getBytes());
	// String c1Str = new Base64().encodeToString(c1);
	// byte[] m = mac.doFinal(c1);
	// String mStr = new Base64().encodeToString(m);
	// return (c1Str + "::::" + mStr).replace("\r\n", "_").replace("\r",
	// "-").replace("\n", "~");
	// } catch (Exception e) {
	// System.out.println("Could not encrypt the message");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }
	//
	// /**
	// * Encrypts the given msg and returns the ciphertext for the
	// * encryted message
	// *
	// * @param msg != null
	// * @param nonce must be a nonce given from the proper sendee
	// * @return the encrypted message or null if encryption fails
	// */
	// public String encryptMsg(String msg, int nonce) {
	// try {
	// cipher.init(Cipher.ENCRYPT_MODE, msgKey);
	// mac.init(msgKey);
	// String nMsg = msg + ":::" + nonce;
	// byte[] c1 = cipher.doFinal(nMsg.getBytes());
	// String c1Str = new Base64().encodeToString(c1);
	// byte[] m = mac.doFinal(c1);
	// String mStr = new Base64().encodeToString(m);
	// return (c1Str + "::::" + mStr).replace("\r\n", "_").replace("\r",
	// "-").replace("\n", "~");
	// } catch (Exception e) {
	// System.out.println("Could not encrypt the message");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }
	//
	// /**
	// * Decrypts the given String and returns the decrypted message if the
	// * message is verified to come from the correct sender, or null otherwise
	// *
	// * @param encryptedMsg != null
	// * @return the decrypted message or null if the message is not verified
	// */
	// public String decryptMsg(String encryptedMsg) {
	// try {
	// cipher.init(Cipher.DECRYPT_MODE, msgKey);
	// mac.init(msgKey);
	// encryptedMsg = encryptedMsg.replace("~", "\n").replace("-",
	// "\r").replace("_", "\r\n");
	// String[] encMsgParts = encryptedMsg.split("::::");
	// String encMsg = encMsgParts[0];
	// String checkM = encMsgParts[1];
	// byte[] encBytes = new Base64().decode(encMsg);
	// byte[] message = cipher.doFinal(encBytes);
	// byte[] m = mac.doFinal(encBytes);
	// String mStr = new Base64().encodeToString(m);
	// if (mStr.equals(checkM))
	// return new String(message);
	// if (DEBUG) {
	// System.out.println("MACs don't match...");
	// }
	// } catch (Exception e) {
	// System.out.println("Could not decrypt");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * Decrypts the given String and returns the decrypted message if the
	// * message is verified to come from the correct sender, or null otherwise
	// *
	// * @param encryptedMsg != null
	// * @param nonce must be the nonce sent to the sender to use in the
	// encryption
	// * of the message
	// * @return the decrypted message or null if the message is not verified
	// */
	// public String decryptMsgNonce(String encryptedMsg) {
	// try {
	// cipher.init(Cipher.DECRYPT_MODE, msgKey);
	// mac.init(msgKey);
	// encryptedMsg = encryptedMsg.replace("~", "\n").replace("-",
	// "\r").replace("_", "\r\n");
	// String[] encMsgParts = encryptedMsg.split("::::");
	// String encMsg = encMsgParts[0];
	// String checkM = encMsgParts[1];
	// byte[] encBytes = new Base64().decode(encMsg);
	// byte[] message = cipher.doFinal(encBytes);
	// byte[] m = mac.doFinal(encBytes);
	// String mStr = new Base64().encodeToString(m);
	// if (mStr.equals(checkM)) {
	// String mess = new String(message);
	// String[] parts = mess.split(":::");
	// String msg = parts[0];
	// int n = Integer.parseInt(parts[1]);
	// if (nonceSet.contains(n)) {
	// nonceSet.remove(n);
	// return msg;
	// } else {
	// if (DEBUG) {
	// System.out.println("Nonces don't match...");
	// System.out.println("Expected: " + nonceSet.toString() + ", Actual: " +
	// n);
	// }
	// return null;
	// }
	// } else {
	// if (DEBUG) {
	// System.out.println("MACs don't match...");
	// }
	// return null;
	// }
	// } catch (Exception e) {
	// System.out.println("Could not decrypt");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * Constructs an RSA key pair and returns the public key or null if the
	// key
	// * pair could not be generated
	// *
	// * @return the RSA public key generated from the construction of the key
	// pair
	// * or null if the key pair could not be generated
	// */
	// public Key getRSAPair() {
	// try {
	// SecureRandom random = new SecureRandom();
	// KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	//
	// generator.initialize(1024, random);
	// KeyPair pair = generator.generateKeyPair();
	// PublicKey pubKey = pair.getPublic();
	// PrivateKey privKey = pair.getPrivate();
	// privRSAKey = privKey;
	// pubRSAKey = pubKey;
	// return pubKey;
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Could not get the RSA pair");
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// }
	//
	// /**
	// * Returns the public RSA information in the form "pubMod pubExp"
	// *
	// * @return a string representation of the RSA public information in the
	// form
	// * "pubMod pubExp"
	// */
	// public String getRSAPubInfo() {
	// String pubKeyStr = pubRSAKey.toString();
	// String[] parts = pubKeyStr.split("\n");
	// String modulus = parts[1].split(": ")[1];
	// String exp = parts[2].split(": ")[1];
	// return modulus + " " + exp;
	// }
	//
	// /**
	// * Generates the RSA public key from the given information. The
	// information
	// * must be RSA public information and must be of the form "pubMod pubExp"
	// *
	// * @param info must be RSA public information and of the form "pubMod
	// pubExp"
	// */
	// public void genRSAPubKey(String info) {
	// try {
	// String[] parts = info.split(" ");
	// BigInteger mod = new BigInteger(parts[0]);
	// BigInteger exp = new BigInteger(parts[1]);
	// KeySpec ks = (KeySpec)new RSAPublicKeySpec(mod, exp);
	// KeyFactory kf = KeyFactory.getInstance("RSA");
	// pubRSAKey = kf.generatePublic(ks);
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Could not generate the RSA public key");
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * Generates the private RSA key from the given information paramter. The
	// * info must be RSA private information and of the form "privMod privExp"
	// *
	// * @param info must be RSA private information and of the form "privMod
	// privExp"
	// */
	// public void genRSAPrivKey(String info) {
	// try {
	// String[] parts = info.split(" ");
	// BigInteger mod = new BigInteger(parts[0]);
	// BigInteger exp = new BigInteger(parts[1]);
	// KeySpec ks = (KeySpec)new RSAPrivateKeySpec(mod, exp);
	// KeyFactory kf = KeyFactory.getInstance("RSA");
	// privRSAKey = kf.generatePrivate(ks);
	// } catch (Exception e) {
	// if (DEBUG) {
	// System.out.println("Could not generate the RSA private key");
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * Encrypts the given message using reverse RSA (encrypts with the private
	// key)
	// * and returns the ciphertext of the message in String form or null if the
	// message
	// * could not be encrypted
	// *
	// * @param msg != null
	// * @return ciphertext form of the message made from RSA encryption or null
	// if the
	// * message could not be encrypted
	// */
	// public String encryptRSA(String msg) {
	// try {
	// Cipher cipher = Cipher.getInstance("RSA");
	// cipher.init(Cipher.ENCRYPT_MODE, privRSAKey);
	// byte[] msgBytes = msg.getBytes();
	// String result = "";
	// for (int i = 0; i < (int)Math.ceil(msgBytes.length * 1.0 /100); i++) {
	// byte[] c = cipher.doFinal(Arrays.copyOfRange(msgBytes, i*100,
	// Math.min((i+1)*100, msgBytes.length)));
	// result += new Base64().encodeToString(c);
	// }
	// return result.replace("\r\n", "_").replace("\r", "~").replace("\n",
	// "::");
	// } catch (Exception e) {
	// System.out.println("Could not encrypt the message with RSA");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }
	//
	// /**
	// * Decrypts the given encrypted message using reverse RSA, meaning that it
	// * uses the public key to decrypt, and returns the decrypte message or
	// null
	// * if the message could not be decrypted
	// *
	// * @param encMsg != null and encrypted with either this object or a
	// similar
	// * object that has the proper decryption parameters
	// * @return the string representation of the message that was decrypted or
	// null
	// * if the message could not be decrypted
	// */
	// public String decryptRSA(String encMsg) {
	// try {
	// encMsg = encMsg.replace("::", "\n").replace("~", "\r").replace("_",
	// "\r\n");
	// Cipher cipher = Cipher.getInstance("RSA");
	// cipher.init(Cipher.DECRYPT_MODE, pubRSAKey);
	// String result = "";
	// for (int i = 0; i < encMsg.length()/178; i++) {
	// String subMsg = encMsg.substring(i*178, Math.min((i+1)*178,
	// encMsg.length()));
	//
	// byte[] msgBytes = cipher.doFinal(new Base64().decode(subMsg));
	// result += new String(msgBytes);
	// }
	// return result;
	// } catch (Exception e) {
	// System.out.println("Could not decrypt the message with RSA");
	// if (DEBUG) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }

}
