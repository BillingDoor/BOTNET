
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
	private List<Pairs<V, T>> list;

	/**
	 * 
	 */
	public SyncIpList() {
		list = new ArrayList<Pairs<V, T>>();
	}

	
	public Integer getSize(){
		return list.size();
	}
	
	/**
	 * @return
	 */
	public List<Pairs<V, T>> getList() {
		synchronized (list) {
			return new ArrayList<Pairs<V,T>>(list);
		}
	}

	/**
	 * @param ips
	 */
	public void setAll(List<Pairs<V, T>> ipList) {
		synchronized (list) {
			list.clear();
			list.addAll(ipList);
		}
	}
	
//	/**
//	 * @param list
//	 *            the ipList to set
//	 */
//	public void setList(List<Pairs<V, T>> list) {
//		this.list = list;
//	}


	/**
	 * @param ips
	 */
	public void addAll(List<Pairs<V, T>> ipList) {
		synchronized (list) {
			list.addAll(ipList);
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public int indexOfValue1(V obj) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).getValue1().equals(obj))
					return i;
			return -1;
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public int indexOfValue2(T obj) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).getValue2().equals(obj))
					return i;
			return -1;
		}
	}
	
	
	/**
	 * @param ipList
	 */
	public void add(Pairs<V, T> ip) {
		synchronized (list) {
			if (list.indexOf(ip) < 0)
				list.add(ip);
		}
	}

	/**
	 * @param obj
	 */
	public void removeByValue1(V obj) {
		synchronized (list) {
			int index = indexOfValue1(obj);
			if (index >= 0)
				list.remove(index);
		}
	}
	
	/**
	 * @param obj
	 */
	public void removeByValue2(T obj) {
		synchronized (list) {
			int index = indexOfValue2(obj);
			if (index >= 0)
				list.remove(index);
		}
	}
	
	


	

}
