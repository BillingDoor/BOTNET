package cs.sii.config.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(
		prefix = "engine",
		locations = "classpath:bot.properties",
		exceptionIfInvalid = true,
		ignoreInvalidFields = false, 
		ignoreUnknownFields = false
		)
public class Engine {
	
		private boolean commandandconquerStatus;
		private int pingdelay;
		private int sleeptime;
		private String dnsip;
		private int dnsport;
		private String urirequest;


		public boolean isCommandandconquerStatus() {
			return commandandconquerStatus;
		}
		public void setCommandandconquerStatus(boolean commandandconquerStatus) {
			this.commandandconquerStatus = commandandconquerStatus;
		}
		public int getPingdelay() {
			return pingdelay;
		}
		public void setPingdelay(int pingdelay) {
			this.pingdelay = pingdelay;
		}
		public int getSleeptime() {
			return sleeptime;
		}
		public void setSleeptime(int sleeptime) {
			this.sleeptime = sleeptime;
		}
		public String getDnsip() {
			return dnsip;
		}
		public void setDnsip(String dnsip) {
			this.dnsip = dnsip;
		}
		public int getDnsport() {
			return dnsport;
		}
		public void setDnsport(int dnsport) {
			this.dnsport = dnsport;
		}
		public String getUrirequest() {
			return urirequest;
		}
		public void setUrirequest(String urirequest) {
			this.urirequest = urirequest;
		}		
		
		
					
}
