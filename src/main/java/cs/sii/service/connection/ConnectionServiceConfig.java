package cs.sii.service.connection;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cs.sii.config.onLoad.Config;

@Configuration
public class ConnectionServiceConfig {

	@Autowired
	Config configEngine;

	@Bean
	public MySSLClientHttpRequestFactory HttpRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		// TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain,
		// String authType) -> true;
		//
		// SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		// .loadTrustMaterial(null, acceptingTrustStrategy)
		// .build();
		//
		// SSLConnectionSocketFactory csf = new
		// SSLConnectionSocketFactory(sslContext);
		//
		// CloseableHttpClient httpClient = HttpClients.custom()
		// .setSSLSocketFactory(csf)
		// .build();

		// SSLConnectionSocketFactory socketFactory = new
		// SSLConnectionSocketFactory(new
		// SSLContextBuilder().loadTrustMaterial(null, new
		// TrustSelfSignedStrategy()).build());

//		 HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

		// ((HttpComponentsClientHttpRequestFactory)
		// template.getRequestFactory()).setHttpClient(httpClient);
		NullHostnameVerifier verifier=new NullHostnameVerifier();
		
		MySSLClientHttpRequestFactory crf = new MySSLClientHttpRequestFactory(verifier);
		
//		HttpComponentsClientHttpRequestFactory crf = new HttpComponentsClientHttpRequestFactory();

//		crf.setHttpClient(httpClient);
		
		crf.setConnectTimeout(configEngine.getConnectTimeout());
//		crf.setConnectionRequestTimeout(configEngine.getRequestTimeout());
		crf.setReadTimeout(configEngine.getReadTimeout());

		return crf;
	    
	
	}

	@Bean
	private static HttpComponentsClientHttpRequestFactory useApacheHttpClientWithSelfSignedSupport() {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
				.build();
		HttpComponentsClientHttpRequestFactory useApacheHttpClient = new HttpComponentsClientHttpRequestFactory();
		useApacheHttpClient.setHttpClient(httpClient);
		return useApacheHttpClient;
	}

	
	@Bean
	public RestTemplate RestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		RestTemplate restTemplate = new RestTemplate(useApacheHttpClientWithSelfSignedSupport());

		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.TEXT_PLAIN);

		MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();
		mc.setSupportedMediaTypes(mediaTypes);
		restTemplate.getMessageConverters().add(mc);
		return restTemplate;
	}

	
	static {
	    disableSslVerification();
	}

	
	private static void disableSslVerification() {
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
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
	
	
}
