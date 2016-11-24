package cs.sii.model.bot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import cs.sii.domain.IP;
import cs.sii.model.user.User;

@Entity
@Table(name="Bot")
public class Bot implements Serializable{
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
		
	@Column(name="idBot", nullable=false)
	private String idBot;
	
	@NotEmpty
	@Column(name="Ip", nullable=false)
	private String ip;
	
	@NotEmpty
	@Column(name="Mac", nullable=false)
	private String mac;
	
	@NotEmpty
	@Column(name="OS", nullable=false)
	private String os;
	
	@ManyToOne
	@JoinColumn(name="User_id",nullable= true)
	private User botUser;

	
	public Bot(String idBot, String ip, String mac, User botUser) {
		super();
		this.idBot = idBot;
		this.ip = ip;
		this.mac = mac;
		this.botUser = botUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdBot() {
		return idBot;
	}

	public void setIdBot(String idBot) {
		this.idBot = idBot;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public User getBotUser() {
		return botUser;
	}

	public void setBotUser(User botUser) {
		this.botUser = botUser;
	}


	
	
}
