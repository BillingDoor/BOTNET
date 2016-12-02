//import java.io.UnsupportedEncodingException;
//import java.security.InvalidKeyException;
//import java.security.KeyFactory;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.security.Signature;
//import java.security.SignatureException;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//
//import org.apache.commons.codec.binary.Base64;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import cs.sii.service.crypto.CryptoPKI;
//import cs.sii.service.crypto.CryptoUtils;
//
//public class TestRsa {
//
//	@Autowired
//	private CryptoPKI cryptoPki;
//	
//	@Autowired
//	private CryptoUtils crypto;
//
//	/** Holds the cipher for this object **/
//	private Cipher cipher;
//
//	private Signature signature;
//
//	/** Holds the private RSA key of this object **/
//	private PrivateKey privERSAKey;
//	/** Holds the public RSA key of this object **/
//	private PublicKey pubERSAKey;
//	
//	public TestRsa()
//			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		 //cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//		cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
//	//	cipher = Cipher.getInstance("RSA/None/PKCS1Padding","BC");
//		
//		signature = Signature.getInstance("SHA512WithRSA", "BC");
//	}
//
//	public void testRSA() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, SignatureException {
//		// byte[] input = "aa".getBytes();
//		
//		//inizializzo generatore chiavi
//		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
//		generator.initialize(4096, new SecureRandom());
//		//genero chiavi
//		KeyPair keyPair = generator.generateKeyPair();
//		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
//		//converto le chiavi secondo i propri standard( questo spesso è già in questa forma ma per renderlo cross platform viene impresso nuovamente)
//		pubERSAKey = fact.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
//		privERSAKey = fact.generatePrivate(new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()));
//		
//	
//		//codifico le chiavi secondo lo standard, le encodiamo in base 64, e poi le scriviamo criptate in aes su file
//		crypto.encodeObjToFile("pub.txt", Base64.encodeBase64String(pubERSAKey.getEncoded()));
//		crypto.encodeObjToFile("priv.txt", Base64.encodeBase64String(privERSAKey.getEncoded()));
//
//		
//			
//		//leggo le chiavi da file e le ricostruisco
//		byte[] encoded = Base64.decodeBase64( crypto.decodeStringFromFile("pub.txt"));
//		byte[] encoded2 = Base64.decodeBase64(crypto.decodeStringFromFile("priv.txt"));
//		pubERSAKey=fact.generatePublic(new X509EncodedKeySpec(encoded));
//		privERSAKey=fact.generatePrivate(new PKCS8EncodedKeySpec(encoded2));
//		
//		//test debug 
//		System.out.println("prE " + pubERSAKey);
//		System.out.println("pErFF " + privERSAKey);
//		System.out.println("cane".getBytes("utf-8"));
//		System.out.println("cane".getBytes("utf-8").length);
//		//test encrypt decrypt
//		String enc=cryptoPki.encryptMessageRSA("gwugcauw",pubERSAKey);		
//		String dec=cryptoPki.decryptMessageRSA(enc);
//		System.out.println("Risultato dog1= "+dec);
//
//		
//	}
//	
//	
//	
//	public void testSign(String dec) throws InvalidKeyException, SignatureException{
//		//test sign
//				String signedText=cryptoPki.signMessageRSA(dec);
//				
//				System.out.println(cryptoPki.validateSignedMessageRSA(dec, signedText, pubERSAKey));
//	}
//	
//}
