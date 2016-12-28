package cs.sii.model.bot;

import java.io.Serializable;
import java.security.PublicKey;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import cs.sii.model.user.User;

@Entity
@Table(name = "Bot")
public class Bot implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "idBot", nullable = false)
	private String idBot;
	@NotEmpty
	@Column(name = "Ip", nullable = false)
	private String ip;
	@NotEmpty
	@Column(name = "Mac", nullable = false)
	private String mac;
	@NotEmpty
	@Column(name = "OS", nullable = false)
	private String os;
	@NotEmpty
	@Column(name = "Version", nullable = false)
	private String ver;
	@NotEmpty
	@Column(name = "Arch", nullable = false)
	private String arch;
	@NotEmpty
	@Column(name = "UsernameOS", nullable = false)
	private String usernameOS;
	@Column(name = "elegible", nullable = false)
	private String elegible;

	
	//	@Type(type="org.hibernate.type.NumericBooleanType")
//	@Convert(converter = BooleanConverter.class)
	

	
	@Column(name = "PubKey", length = 5000)
	@Convert(converter = KeyConverter.class)
	private PublicKey pubKey;

	@ManyToOne
	@JoinColumn(name = "User_id", nullable = true)
	private User botUser;

	public Bot() {

	}

	public Bot(String idBot, String ip, String mac, String os, String ver, String arch, String usernameOS, PublicKey pk,
			User botUser, String elegible) {
		super();
		this.idBot = idBot;
		this.ip = ip;
		this.mac = mac;
		this.os = os;
		this.ver = ver;
		this.arch = arch;
		this.usernameOS = usernameOS;
		this.pubKey = pk;
		this.botUser = botUser;
		this.elegible = elegible;
	}

	public Bot(String idBot, String ip, String mac, String os, String ver, String arch, String usernameOS, PublicKey pk,
			String elegible) {
		super();
		this.idBot = idBot;
		this.ip = ip;
		this.mac = mac;
		this.os = os;
		this.ver = ver;
		this.arch = arch;
		this.usernameOS = usernameOS;
		this.pubKey = pk;
		this.elegible = elegible;
		this.botUser = null;
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

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getUsernameOS() {
		return usernameOS;
	}

	public void setUsernameOS(String usernameOS) {
		this.usernameOS = usernameOS;
	}

	public User getBotUser() {
		return botUser;
	}

	public void setBotUser(User botUser) {
		this.botUser = botUser;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	public String isElegible() {
		return elegible;
	}

	public void setElegible(String elegible) {
		this.elegible = elegible;
	}



}
