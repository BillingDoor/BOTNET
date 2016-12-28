package cs.sii.config.onLoad;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;


@Component
public class Initialize {


	@Autowired
	private NetworkService nServ;

	@Autowired
	private CryptoPKI pki;
	
	
	/**
	 * Load info from machine and from files
	 */
	public void loadInfo() {

		// System.out.println(folder.canExecute());
		// System.out.println(folder.getCanonicalPath());
		try {

			pki.loadKeyFromFile();
			nServ.loadNetwork();
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
				| InvalidKeySpecException e) {
			if ((pki.getPrivRSAKey() == null) || (Base64.encodeBase64String(pki.getPrivRSAKey().getEncoded()) == ""))
				pki.generateKeyRSA();
			if ((nServ.getIdHash() == null) || (nServ.getIdHash() == ""))
				nServ.getMachineInfo();
			e.printStackTrace();
		}


	}

}
