package cs.sii.config.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
		prefix = "jpa.database",
		locations = "classpath:jpa.properties",
		exceptionIfInvalid = true,
		ignoreInvalidFields = false, 
		ignoreUnknownFields = false
		)
public class Database {
	private String url;
	private String driverClass;
	private String username;
	private String password;
	private String name;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
	
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
	
		this.url = url;
	}
	
	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
	
		return driverClass;
	}
	
	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
	
		this.driverClass = driverClass;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
	
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
	
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
	
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
	
		this.password = password;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
	
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
	
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "Database [url=" + url + ", driverClass=" + driverClass + ", username=" + username + ", password=" + password + ", name=" + name + "]";
	}
	
	

}
