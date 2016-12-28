package cs.sii.security;

public class DatabaseEncryption {

//	
//
//import java.security.Key;
//import java.util.Properties;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.crypto.codec.Base64;
//
//@Converter
//public class JPACryptoConverter implements AttributeConverter<String, String> {
//
//	static Logger logger = LoggerFactory.getLogger(JPACryptoConverter.class);
//
//	private static String ALGORITHM = null;
//	private static byte[] KEY = null;
//
//	public static final String algorithm_property_key = "encryption.algorithm";
//	public static final String secret_property_key = "encryption.key";
//
//	static final Properties properties = new Properties();
//	static {
//		try {
//			properties.load(JPACryptoConverter.class.getClassLoader()
//					.getResourceAsStream("persistence.properties"));
//		} catch (Exception e) {
//			logger.warn("Could not load properties file 'persistence.properties' using unsecure encryption key.");
//			properties.put(algorithm_property_key, "AES/ECB/PKCS5Padding");
//			properties.put(secret_property_key, "MySuperSecretKey");
//		}
//		ALGORITHM = (String) properties.get(algorithm_property_key);
//		KEY = ((String) properties.get(secret_property_key)).getBytes();
//	}
//
//	@Override
//	public String convertToDatabaseColumn(String sensitive) {
//		Key key = new SecretKeySpec(KEY, "AES");
//		try {
//			final Cipher c = Cipher.getInstance(ALGORITHM);
//			c.init(Cipher.ENCRYPT_MODE, key);
//			final String encrypted = new String(Base64.encode(c
//					.doFinal(sensitive.getBytes())), "UTF-8");
//			return encrypted;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public String convertToEntityAttribute(String sensitive) {
//		Key key = new SecretKeySpec(KEY, "AES");
//		try {
//			final Cipher c = Cipher.getInstance(ALGORITHM);
//			c.init(Cipher.DECRYPT_MODE, key);
//			final String decrypted = new String(c.doFinal(Base64
//					.decode(sensitive.getBytes("UTF-8"))));
//			return decrypted;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//}
//Raw
// SrpAccountEntity.java
//
//
//import javax.persistence.Column;
//import javax.persistence.Convert;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.NamedQuery;
//import javax.persistence.Table;
//
///**
// * This is both an entity and a cached key
// */
//@SuppressWarnings("serial")
//@Entity
//@Table(name = "account")
//@NamedQuery(name = SrpAccountEntity.FIND_BY_EMAIL, query = "select a from SrpAccountEntity a where a.email = :email")
//public class SrpAccountEntity implements java.io.Serializable {
//
//	public static final String FIND_BY_EMAIL = "SrpAccountEntity.findByEmail";
//
//	@Id
//	@Column(unique = true)
//	private String email;
//	
//	@Column(unique = true)
//	private String salt;
//
//	@Column(columnDefinition = "CLOB")
//	@Convert(converter = JPACryptoConverter.class)
//	private String verifier;
//
//	private String role = "ROLE_USER";
//
//    protected SrpAccountEntity() {
//
//	}
//	
//	public SrpAccountEntity(String email, String salt, String verifier,
//			String role) {
//		this.email = email;
//		this.salt = salt;
//		this.verifier = verifier;
//		this.role = role;
//	}
//
//    public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getVerifier() {
//		return verifier;
//	}
//
//	public void setVerifier(String verifier) {
//		this.verifier = verifier;
//	}
//
//	public String getSalt() {
//		return salt;
//	}
//
//	public void setSalt(String salt) {
//		this.salt = salt;
//	}
//
//	public String getRole() {
//		return role;
//	}
//
//	public void setRole(String role) {
//		this.role = role;
//	}
//
//}
}
