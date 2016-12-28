package cs.sii.model.bot;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.beans.factory.annotation.Autowired;

import cs.sii.service.crypto.CryptoPKI;

@Converter
public class KeyConverter implements AttributeConverter<PublicKey, String> {

	@Autowired
	private CryptoPKI pki;

	/**
	 * Convert PublicKey Object to String
	 */
	@Override
	public String convertToDatabaseColumn(PublicKey key) {

		try {
			if(key!=null){
				String s = pki.demolishPuK(key);
				System.out.println("conversion" + s.length());
				return s;
			}
			System.out.println("conversione fallita");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			System.out.println("errore convert to DB");
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Convert a String to PublicKey Object
	 */
	@Override
	public PublicKey convertToEntityAttribute(String keyEncoding) {
		try {
			if ((keyEncoding != null))
				return pki.rebuildPuK(keyEncoding);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			System.out.println("errore convert from DB");
			e.printStackTrace();
		}
		return null;
	}

}