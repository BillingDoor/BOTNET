package cs.sii.domain;

public class LinkBot {

    Long id;
	
	private String botL;
	
	private String usrL;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdBot() {
		return botL;
	}

	public void setIdBot(String idBot) {
		botL = idBot;
	}

	public String getSsoId() {
		return usrL;
	}

	public void setSsoId(String ssoId) {
		this.usrL = ssoId;
	}

}
