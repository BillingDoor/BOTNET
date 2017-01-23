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

import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class MySSLClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

//    private final HostnameVerifier verifier;
//    private final String cookie="";

    public MySSLClientHttpRequestFactory(HostnameVerifier verifier) {
       // this.verifier = verifier;
    	mySslVerification(verifier);
    }


    
    private static void mySslVerification(HostnameVerifier verifier) {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	        
	            @Override
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

	            @Override
	            public void checkClientTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	            	System.out.println("ssl client");
	            }

	            @Override
	            public void checkServerTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType)
	                    throws CertificateException {
//	                InputStream inStream = null;
//	                System.out.println("ssl");
//	                try {
//	                    // Loading the CA cert
//	                    URL u = getClass().getResource("classpath:/cac.pem");
//	                    inStream = new FileInputStream(u.getFile());
//		                System.out.println("ssl loaded");
//
//	                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
//	                    X509Certificate ca = (X509Certificate) cf.generateCertificate(inStream);
//	                    inStream.close();
//		                System.out.println("ssl builded");
//
//	                    for (X509Certificate cert : certs) {
//	                        // Verifing by public key
//	                        cert.verify(ca.getPublicKey());
//	    	                System.out.println("ssl verify certs");
//
//	                    }
//		                System.out.println("ssl verified");
//
//	                } catch (Exception ex) {
//	                	System.out.println("erroe validazione cert");
//	                } finally {
//	                    try {
//	                        inStream.close();
//	                    } catch (IOException ex) {
//	                    }
//	                }

	            }
	        }
	        };


	        
	        
	        try { 
	        KeyManagerFactory kmf;
	        KeyStore ks;
	        char[] storepass = "sicurezza2016".toCharArray();
	        char[] keypass = "sicurezza2016".toCharArray();
	        String storename = "src/main/resources/SIIKeyStore.jks";

	        kmf = KeyManagerFactory.getInstance("SunX509");
	        FileInputStream fin = new FileInputStream(storename);
	        
			ks = KeyStore.getInstance("JKS");
			
	        ks.load(fin, storepass);

	        kmf.init(ks, keypass);

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("TLS");
	        
	        sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
	        
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
	        } catch (KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
    
    
}



//    @Override
//    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
//        if (connection instanceof HttpsURLConnection) {
//            ((HttpsURLConnection) connection).setDefaultHostnameVerifier(verifier);
//            ((HttpsURLConnection) connection).setDefaultSSLSocketFactory(trustSelfSignedSSL().getSocketFactory());
//            ((HttpsURLConnection) connection).setAllowUserInteraction(true);
//        }
//        super.prepareConnection(connection, httpMethod);
//    }

//    public SSLContext trustSelfSignedSSL() {
//        try {
//            X509TrustManager tm = new X509TrustManager() {
//
//                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };
//            SSLContext ctx = SSLContext.getInstance("TLS");
//            ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
//            SSLContext.setDefault(ctx);
//            return ctx;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }






