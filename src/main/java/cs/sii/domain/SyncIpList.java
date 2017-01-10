
package cs.sii.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Classe che si occupa di gestire gli accessi alla lista
 * 
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

		// for (Pairs<V, T> pairs : list) {
		// System.out.println("construct sync ip "+pairs.getValue1() );
		// }
	}

	public Integer getSize() {
		Integer x = 0;
		synchronized (list) {
			x = list.size();
		}

		// for (Pairs<V, T> pairs : list) {
		// System.out.println("size sync ip "+pairs.getValue1() );
		// }
		return x;
	}

	// /**
	// * @return
	// */
	// public List<Pairs<V, T>> getList() {
	// synchronized (list) {
	// return list;
	// }
	// }

	
	
	
	/**
	 * @param ips
	 */
	public void setAll(List<Pairs<V, T>> ipList) {
		synchronized (list) {
			// for (Pairs<V, T> pairs : list) {
			// System.out.println("setAllOLD sync ip "+pairs.getValue1() );
			// }
			list.clear();
			list.addAll(ipList);
			// for (Pairs<V, T> pairs : ipList) {
			// System.out.println("setAllNEW sync ip "+pairs.getValue1() );
			// }
		}
	}

	// /**
	// * @param list
	// * the ipList to set
	// */
	// public void setList(List<Pairs<V, T>> list) {
	// this.list = list;
	// }

	/**
	 * @param ips
	 */
	public void addAll(List<Pairs<V, T>> ipList) {
		synchronized (list) {
			// for (Pairs<V, T> pairs : list) {
			// System.out.println("addALLOLD sync ip "+pairs.getValue1() );
			// }
			// for (Pairs<V, T> pairs : ipList) {
			// System.out.println("AddAllNew sync ip "+pairs.getValue1() );
			// }
			list.addAll(ipList);
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public int indexOf(Pairs<V, T> obj) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).equals(obj))
					return i;
			return -1;
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
			if (list.indexOf(ip) < 0) {
				list.add(ip);
				// System.out.println("sync ip Add "+ip.getValue1());
			}
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public Pairs<V, T> remove(Pairs<V, T> obj) {
		synchronized (list) {
			int index = indexOf(obj);
			if (index >= 0)
				return list.remove(index);
		}
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	public Pairs<V, T> remove(int i) {
		synchronized (list) {
			return list.remove(i);
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public Pairs<V, T> removeByValue1(V obj) {
		synchronized (list) {
			int index = indexOfValue1(obj);
			if (index >= 0)
				return list.remove(index);
		}
		return null;
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

	public Pairs<V, T> getByValue1(V obj) {
		synchronized (list) {
			int index = indexOfValue1(obj);
			return list.get(index);
		}
	}

	public Pairs<V, T> getByValue2(T obj) {
		synchronized (list) {
			int index = indexOfValue2(obj);
			return list.get(index);
		}
	}

	public Pairs<V, T> get(Pairs<V, T> obj) {
		synchronized (list) {
			int index = indexOf(obj);
			return list.get(index);
		}
	}

	public Pairs<V, T> get(int i) {
		synchronized (list) {
			return list.get(i);
		}
	}
	


}
