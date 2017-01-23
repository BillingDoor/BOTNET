package cs.sii.domain;

public class LinkBot {

	private Integer idBotL;

	private Integer idUsrL;

	public LinkBot() {
	}

	public LinkBot(Integer botL, Integer usrL) {
		super();
		this.idBotL = botL;
		this.idUsrL = usrL;
	}

	public Integer getIdBotL() {
		return idBotL;
	}

	public void setIdBotL(Integer idBotL) {
		this.idBotL = idBotL;
	}

	public Integer getIdUsrL() {
		return idUsrL;
	}

	public void setIdUsrL(Integer idUsrL) {
		this.idUsrL = idUsrL;
	}

}
