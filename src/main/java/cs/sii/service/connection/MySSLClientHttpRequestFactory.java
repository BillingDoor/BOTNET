package cs.sii.service.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class MySSLClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

//    private final HostnameVerifier verifier;
//    private final String cookie="";

    public MySSLClientHttpRequestFactory(HostnameVerifier verifier) {
       // this.verifier = verifier;
    	disableSslVerification(verifier);
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

    public SSLContext trustSelfSignedSSL() {
        try {
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
            SSLContext.setDefault(ctx);
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    
    private static void disableSslVerification(HostnameVerifier verifier) {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        

	        // Create all-trusting host name verifier
//	        HostnameVerifier allHostsValid = new HostnameVerifier() {
//	            public boolean verify(String hostname, SSLSession session) {
//	                return true;
//	            }
//	        };
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
    
    
}


