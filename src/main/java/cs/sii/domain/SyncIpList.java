
package cs.sii.domain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import cs.sii.model.bot.Bot;

/**
 * Classe che si occupa di gestire gli accessi alla lista CeC
 * @param <V>
 * @param <T>
 *
 */

@Service
public class SyncIpList<V, T> {
	private List<Pairs<V, T>> List;

	/**
	 * 
	 */
	public SyncIpList() {
		List = new ArrayList<Pairs<V, T>>();
	}

	
	public Integer getSize(){
		return List.size();
	}
	
	/**
	 * @return
	 */
	public List<Pairs<V, T>> getList() {
		synchronized (List) {
			return new ArrayList<>(List);
		}
	}

	/**
	 * @param ips
	 */
	public void setAll(List<Pairs<V, T>> ipList) {
		synchronized (List) {
			List.clear();
			List.addAll(ipList);
		}
	}

	/**
	 * @param ips
	 */
	public void addAll(List<Pairs<V, T>> ipList) {
		synchronized (List) {
			List.addAll(ipList);
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public int indexOfValue1(V obj) {
		synchronized (List) {
			for (int i = 0; i < List.size(); i++)
				if (List.get(i).getValue1().equals(obj))
					return i;
			return -1;
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public int indexOfValue2(T obj) {
		synchronized (List) {
			for (int i = 0; i < List.size(); i++)
				if (List.get(i).getValue2().equals(obj))
					return i;
			return -1;
		}
	}
	
	
	/**
	 * @param ipList
	 */
	public void add(Pairs<V, T> ipList) {
		synchronized (List) {
			if (List.indexOf(ipList.getValue1()) < 0)
				List.add(ipList);
		}
	}

	/**
	 * @param obj
	 */
	public void removeByValue1(V obj) {
		synchronized (List) {
			int index = indexOfValue1(obj);
			if (index >= 0)
				List.remove(index);
		}
	}
	
	/**
	 * @param obj
	 */
	public void removeByValue2(T obj) {
		synchronized (List) {
			int index = indexOfValue2(obj);
			if (index >= 0)
				List.remove(index);
		}
	}
	
	
	/**
	 * @param list
	 *            the ipList to set
	 */
	public void setList(List<Pairs<V, T>> list) {
		this.List = list;
	}


	

}
