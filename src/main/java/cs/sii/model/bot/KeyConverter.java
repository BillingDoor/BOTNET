package cs.sii.model.bot;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import cs.sii.service.crypto.CryptoPKI;

@Converter
public class KeyConverter implements AttributeConverter<PublicKey, String> {


	/**
	 * Convert PublicKey Object to String
	 */
	@Override
	public String convertToDatabaseColumn(PublicKey key) {
		System.out.println("converterTODB");
		if(key!=null){
			String s=Base64.encodeBase64String(key.getEncoded());
			System.out.println("conversion" + s.length());
			return s;
		}
		System.out.println("conversione fallita");
		return "";
	}

	/**
	 * Convert a String to PublicKey Object
	 */
	@Override
	public PublicKey convertToEntityAttribute(String keyEncoding) {
		System.out.println("converterFromDB"+keyEncoding);
		
		try {KeyFactory  fact = KeyFactory.getInstance("RSA", "BC");
			if ((keyEncoding != null)){
				 PublicKey puK = fact.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(keyEncoding)));		
				 System.out.println("PD");
				return puK;
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
			System.out.println("errore convert from DB");
			e.printStackTrace();
		}
		return null;
	}

}