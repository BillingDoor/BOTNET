package cs.sii.service.crypto;

import java.io.UnsupportedEncodingException;
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
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptoPKI {

	@Autowired
	private CryptoUtils crypto;

	/** Holds the cipher for this object **/
	private Cipher cipher;

	private Signature signature;

	/** Holds the private RSA key of this object **/
	private PrivateKey privRSAKey;
	/** Holds the public RSA key of this object **/
	private PublicKey pubRSAKey;

	/** Holds the private RSA key of this object **/
	private PrivateKey privERSAKey;
	/** Holds the public RSA key of this object **/
	private PublicKey pubERSAKey;

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
		 //cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
	//	cipher = Cipher.getInstance("RSA/None/PKCS1Padding","BC");
		
		signature = Signature.getInstance("SHA512WithRSA", "BC");
	}

	public void generateKeyRSA() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		// byte[] input = "aa".getBytes();

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		generator.initialize(4096, new SecureRandom());

		KeyPair keyPair = generator.generateKeyPair();
		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");

		pubERSAKey = fact.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
		privERSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()));
		
		pubRSAKey =pubERSAKey;
		privRSAKey = privERSAKey;//privERSAKey;//
	
		
		String encryptedValue = Base64.encodeBase64String(pubRSAKey.getEncoded());
		String encryptedValue2 = Base64.encodeBase64String(pubERSAKey.getEncoded());
		String encryptedValue3 = Base64.encodeBase64String(privRSAKey.getEncoded());
		String encryptedValue4 = Base64.encodeBase64String(privERSAKey.getEncoded());

		ArrayList<Object> pr = new ArrayList<Object>();
		ArrayList<Object> pr2 = new ArrayList<Object>();
		pr.add(encryptedValue);
		pr.add(encryptedValue2);
		pr2.add(encryptedValue3);
		pr2.add(encryptedValue4);
		crypto.encodeObjToFile("pub.txt", pr);
		crypto.encodeObjToFile("priv.txt", pr2);

		ArrayList<String> prB = crypto.decodeStringFromFile("pub.txt");
		ArrayList<String> prB2 = crypto.decodeStringFromFile("priv.txt");
		byte[] encoded = Base64.decodeBase64(prB.get(0));
		pubRSAKey=fact.generatePublic(new X509EncodedKeySpec(encoded));
	
		byte[] encoded2 = Base64.decodeBase64(prB.get(1));
		pubRSAKey=pubERSAKey=fact.generatePublic(new X509EncodedKeySpec(encoded2));
	
		byte[] encoded3 = Base64.decodeBase64(prB2.get(0));
		privRSAKey=fact.generatePrivate(new PKCS8EncodedKeySpec(encoded3));
	
		byte[] encoded4 = Base64.decodeBase64(prB2.get(1));
		privRSAKey=privERSAKey=fact.generatePrivate(new PKCS8EncodedKeySpec(encoded4));
	
				
			
			

		System.out.println("pr " + pubRSAKey);
		System.out.println("prE " + pubERSAKey);
		System.out.println("pErFF " + privERSAKey);
		System.out.println("prFF " + privRSAKey);
		System.out.println("cane".getBytes("utf-8"));
		System.out.println("cane".getBytes("utf-8").length);
		
		
		String enc=encryptMessageRSA("gwugcauw",pubERSAKey);		
		String dec=decryptMessageRSA(enc);
		System.out.println("Risultato dog1= "+dec);
		
		// X509EncodedKeySpec publicKeySpec = new
		// X509EncodedKeySpec(prB.get(0).getBytes());
		//
		// System.out.println("prFF " +
		// fact.generatePublic(publicKeySpec).toString());

	}

	private PublicKey rebuildPuK(String keyEncoding) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encoded = Base64.decodeBase64(keyEncoding);
		PublicKey puK = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
		return puK;
	}
	// public String getPrKey(KeyPair keys) {
	// PrivateKey prKey = keys.getPrivate();
	// byte[] encodedKey = prKey.getEncoded();
	// return Base64.encodeBase64String(encodedKey);
	// }
	//
	// public String getPuKey(KeyPair keys) {
	// PublicKey puKey = keys.getPublic();
	// byte[] encodedKey = puKey.getEncoded();
	// return Base64.encodeBase64String(encodedKey);
	// }

	public String encryptMessageRSA(String input, PublicKey receiverPubKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		
		cipher.init(Cipher.ENCRYPT_MODE, receiverPubKey);
		byte[] inputByte = input.getBytes("utf-8");
		byte[] cipherText = cipher.doFinal(inputByte);
		String encryptedValue = Base64.encodeBase64String(cipherText);
		System.out.println("cipher: " + encryptedValue);
		return encryptedValue;
	}

	// public String cifra(String key, String msg) throws Exception {
	// Cipher cipher = Cipher.getInstance("RSA");
	// cipher.init(Cipher.ENCRYPT_MODE, rebuildPrK(key));
	// byte[] encVal = cipher.doFinal(msg.getBytes());
	// String encryptedValue = Base64.encodeBase64String(encVal);
	// return encryptedValue;
	// }

	public String decryptMessageRSA(String cipherText)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		cipher.init(Cipher.DECRYPT_MODE, privRSAKey);
		byte[] decodedValue = Base64.decodeBase64(cipherText);
		byte[] plainText = cipher.doFinal(decodedValue);
		System.out.println("plain : " + new String(plainText));
		return new String(plainText,"utf-8");
	}

	// public String decifra(String key, String msg) throws InvalidKeyException,
	// IllegalBlockSizeException,
	// BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException,
	// InvalidKeySpecException {
	// Cipher cipher = Cipher.getInstance("RSA");
	// cipher.init(Cipher.DECRYPT_MODE, rebuildPuK(key));
	// byte[] decordedValue = Base64.decodeBase64(msg);
	// byte[] decValue = cipher.doFinal(decordedValue);
	// String decryptedValue = new String(decValue);
	// return decryptedValue;
	// }

	public byte[] signMessageRSA(String message) throws SignatureException, InvalidKeyException {
		signature.initSign(privRSAKey);
		byte[] msg = message.getBytes();
		signature.update(msg);
		byte[] sigBytes = signature.sign();
		return sigBytes;
	}

	public Boolean validateSignedMessageRSA(String clearMessage, byte[] cipherMessage, PublicKey senderPubKey)
			throws InvalidKeyException, SignatureException {
		byte[] msg = clearMessage.getBytes();
		byte[] sigBytes = cipherMessage;
		signature.initVerify(senderPubKey);
		signature.update(msg);
		return signature.verify(sigBytes);
	}

	// NUll
	private PrivateKey rebuildPrK(String keyEncoding) throws Exception {
		byte[] encoded = Base64.decodeBase64(keyEncoding);
		PrivateKey prK = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
		return prK;
	}

	// public void SaveKeyPair(String path, KeyPair keyPair) throws IOException
	// {
	// PrivateKey privateKey = keyPair.getPrivate();
	// PublicKey publicKey = keyPair.getPublic();
	//
	// // Store Public Key.
	// X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
	// publicKey.getEncoded());
	// FileOutputStream fos = new FileOutputStream(path + "/public.key");
	// fos.write(x509EncodedKeySpec.getEncoded());
	// fos.close();
	//
	// // Store Private Key.
	// PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
	// privateKey.getEncoded());
	// fos = new FileOutputStream(path + "/private.key");
	// fos.write(pkcs8EncodedKeySpec.getEncoded());
	// fos.close();
	// }
	//
	// public KeyPair LoadKeyPair(String path, String algorithm)
	// throws IOException, NoSuchAlgorithmException,
	// InvalidKeySpecException {
	// // Read Public Key.
	// File filePublicKey = new File(path + "/public.key");
	// FileInputStream fis = new FileInputStream(path + "/public.key");
	// byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
	// fis.read(encodedPublicKey);
	// fis.close();
	//
	// // Read Private Key.
	// File filePrivateKey = new File(path + "/private.key");
	// fis = new FileInputStream(path + "/private.key");
	// byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
	// fis.read(encodedPrivateKey);
	// fis.close();
	//
	// // Generate KeyPair.
	// KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
	// X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
	// encodedPublicKey);
	// PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
	//
	// PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
	// encodedPrivateKey);
	// PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
	//
	// return new KeyPair(publicKey, privateKey);
	// }

	public PublicKey getPubRSAKey() {
		return pubRSAKey;
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
