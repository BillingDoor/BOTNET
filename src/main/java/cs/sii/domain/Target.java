package cs.sii.domain;

public class Target {

	private String typeAttack;
	private String ipDest;
	private Integer portDest;
	private Integer timeToAttack;

	public Target() {
	}

	public Target(String typeAttack) {
		super();
		this.typeAttack = typeAttack;
	}

	public String getTypeAttack() {
		return typeAttack;
	}

	public void setTypeAttack(String typeAttack) {
		this.typeAttack = typeAttack;
	}

	public String getIpDest() {
		return ipDest;
	}

	public void setIpDest(String ipDest) {
		this.ipDest = ipDest;
	}

	public Integer getPortDest() {
		return portDest;
	}

	public void setPortDest(Integer portDest) {
		this.portDest = portDest;
	}

	public Integer getTimeToAttack() {
		return timeToAttack;
	}

	public void setTimeToAttack(Integer timeToAttack) {
		this.timeToAttack = timeToAttack;
	}

}
