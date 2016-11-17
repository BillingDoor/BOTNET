package cs.sii.dao;

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

import cs.sii.model.IP;

@Entity
@Table(name="Bot")
public class Botter implements Serializable{
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
		
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

	
	public Botter(String idBot, String ip, String mac, String os) {
		this.id=idBot;
		this.ip=ip;
		this.mac=mac;
		this.os=os;
		botUser=null;
	}


	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public User getBotUser() {
		return botUser;
	}

	public void setBotUser(User botUser) {
		this.botUser = botUser;
	}


	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}
	
	
	
	
}
