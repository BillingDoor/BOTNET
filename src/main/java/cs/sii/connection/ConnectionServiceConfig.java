package cs.sii.connection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cs.sii.config.bot.Engine;

@Configuration
public class ConnectionServiceConfig {

	@Autowired
	Engine engineBot;
	
	@Bean
	public HttpComponentsClientHttpRequestFactory HttpRequestFactory() {


		HttpComponentsClientHttpRequestFactory crf = new HttpComponentsClientHttpRequestFactory();
		crf.setConnectTimeout(engineBot.getConnectTimeout());
		crf.setConnectionRequestTimeout(engineBot.getRequestTimeout());
		crf.setReadTimeout(engineBot.getReadTimeout());
		return crf;
	}

	@Bean
	public RestTemplate RestTemplate() {

		RestTemplate restTemplate = new RestTemplate(HttpRequestFactory());

		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.TEXT_PLAIN);

		MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();
		mc.setSupportedMediaTypes(mediaTypes);

		restTemplate.getMessageConverters().add(mc);
		return restTemplate;
	}

	
}


