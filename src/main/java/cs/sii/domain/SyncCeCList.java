
package cs.sii.domain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Classe che si occupa di gestire gli accessi alla lista CeC
 *
 */

@Service
public class SyncCeCList {
	private List<Pairs<IP, PublicKey>> cecList;

	/**
	 * 
	 */
	public SyncCeCList() {
		cecList = new ArrayList<Pairs<IP, PublicKey>>();
	}

	/**
	 * @return
	 */
	public List<Pairs<IP, PublicKey>> getCeCList() {
		synchronized (cecList) {
			return new ArrayList<>(cecList);
		}
	}

	/**
	 * @param ips
	 */
	public void setAllCeC(List<Pairs<IP, PublicKey>> cec) {
		synchronized (cecList) {
			cecList.clear();
			cecList.addAll(cec);
		}
	}

	
	/**
	 * @param ips
	 */
	public void addAllCeC(List<Pairs<IP, PublicKey>> cec) {
		synchronized (cecList) {
		 cecList.addAll(cec);
		}
	}
	
	/**
	 * @param ip
	 * @return
	 */
	public int indexOf(String ip) {
		synchronized (cecList) {
			for (int i = 0; i < cecList.size(); i++)
				if (cecList.get(i).getValue1().getIp().equals(ip))
					return i;
			return -1;
		}
	}

	/**
	 * @param ip
	 */
	public void addCeC(Pairs<IP, PublicKey> cec) {
		synchronized (cecList) {
			if (cecList.indexOf(cec.getValue1().getIp()) < 0)
				cecList.add(cec);
		}
	}

	/**
	 * @param ip
	 */
	public void removeCeC(IP ip) {
		synchronized (cecList) {
			int index = indexOf(ip.getIp());
			if (index >= 0)
				cecList.remove(index);
		}
	}

	/**
	 * @param ipList
	 *            the ipList to set
	 */
	public void setCeCList(List<Pairs<IP, PublicKey>> cecList) {
		this.cecList = cecList;
	}

}
