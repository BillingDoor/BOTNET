package cs.sii.config.persistence;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
		value = "hibernate",
		locations = "classpath:hibernate.properties",
		exceptionIfInvalid = true,
		ignoreInvalidFields = false, 
		ignoreUnknownFields = false
		)
public class Hibernate {
	private String default_catalog;
	private Hbm2ddl hbm2ddl;
	private String dialect;
	private String show_sql;
	
	public static class Hbm2ddl{
		private String auto;

		public String getAuto() {
			return auto;
		}

		public void setAuto(String auto) {
			this.auto = auto;
		}
		
	}
	public Hbm2ddl getHbm2ddl() {
		return hbm2ddl;
	}
	public void setHbm2ddl(Hbm2ddl hbm2ddl) {
		this.hbm2ddl = hbm2ddl;
	}
	
	
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public String getShow_sql() {
		return show_sql;
	}
	public void setShow_sql(String show_sql) {
		this.show_sql = show_sql;
	}
	public String getDefault_catalog() {
		return default_catalog;
	}
	public void setDefault_catalog(String default_catalog) {
		this.default_catalog = default_catalog;
	}
	@Override
	public String toString() {
		return "Hibernate [default_catalog=" + default_catalog + ", hbm2ddl=" + hbm2ddl + ", dialect=" + dialect
				+ ", show_sql=" + show_sql + "]";
	}

	

}
