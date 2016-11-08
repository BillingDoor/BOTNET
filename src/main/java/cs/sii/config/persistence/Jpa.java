package cs.sii.config.persistence;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(
		value = "jpa",
		locations = "classpath:jpa.properties",
		exceptionIfInvalid = true,
		ignoreInvalidFields = false, 
		ignoreUnknownFields = false
		)
public class Jpa {
	@NestedConfigurationProperty
	private List<String> packagesToScan;
	
	@NestedConfigurationProperty
	private Database database;

	public List<String> getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(List<String> packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	@Override
	public String toString() {
		return "Jpa [packagesToScan=" + packagesToScan + ", database=" + database + "]";
	}
	


}
