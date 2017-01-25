package cs.sii.service.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.hibernate.jpa.boot.internal.FileInputStreamAccess;
import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class MySSLClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

	// private final HostnameVerifier verifier;
	// private final String cookie="";

	public MySSLClientHttpRequestFactory(HostnameVerifier verifier) {
		// this.verifier = verifier;
		mySslVerification(verifier);
	}

	private static void mySslVerification(HostnameVerifier verifier) {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };

			try {
				KeyManagerFactory kmf;
				KeyStore ks;
				char[] storepass = "sicurezza2016".toCharArray();
				char[] keypass = "sicurezza2016".toCharArray();
			
				
				String storename = "/SIIKeyStore.jks";
			
				kmf = KeyManagerFactory.getInstance("SunX509");
//				FileInputStream fin = new FileInputStream(storename);

				ResourcePatternResolver patternResolver=new PathMatchingResourcePatternResolver();
				Resource resource=patternResolver.getResource(storename);
				
				
				
				ks = KeyStore.getInstance("JKS");

				ks.load(resource.getInputStream(), storepass);

				kmf.init(ks, keypass);

				// Install the all-trusting trust manager
				SSLContext sc = SSLContext.getInstance("TLS");

				sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());

				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				// Install the all-trusting host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(verifier);
				
				
				SSLContext.setDefault(sc);
				
			} catch (KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

}

// @Override
// protected void prepareConnection(HttpURLConnection connection, String
// httpMethod) throws IOException {
// if (connection instanceof HttpsURLConnection) {
// ((HttpsURLConnection) connection).setDefaultHostnameVerifier(verifier);
// ((HttpsURLConnection)
// connection).setDefaultSSLSocketFactory(trustSelfSignedSSL().getSocketFactory());
// ((HttpsURLConnection) connection).setAllowUserInteraction(true);
// }
// super.prepareConnection(connection, httpMethod);
// }

// public SSLContext trustSelfSignedSSL() {
// try {
// X509TrustManager tm = new X509TrustManager() {
//
// public void checkClientTrusted(X509Certificate[] xcs, String string) throws
// CertificateException {
// }
//
// public void checkServerTrusted(X509Certificate[] xcs, String string) throws
// CertificateException {
// }
//
// public X509Certificate[] getAcceptedIssuers() {
// return null;
// }
// };
// SSLContext ctx = SSLContext.getInstance("TLS");
// ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
// SSLContext.setDefault(ctx);
// return ctx;
// } catch (Exception ex) {
// ex.printStackTrace();
// }
// return null;
// }
