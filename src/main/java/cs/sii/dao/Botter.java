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
@Table(name="Botter")
public class Botter implements Serializable{
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	@Column(name="Ip", nullable=false)
	private String ip;
	
	
	//TODO bisogna cambiare  emttere la relazione many to many
	// poiche ogni bot può essere assegnato a più utenti
	@ManyToOne
	@JoinColumn(name="User_id")
	private User botUser;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	
	
	
}
