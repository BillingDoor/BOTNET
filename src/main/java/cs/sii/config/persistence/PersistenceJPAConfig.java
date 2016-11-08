package cs.sii.config.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("cs.sii.dao")
@EntityScan("cs.sii")
public class PersistenceJPAConfig {

	@Autowired
	private Jpa propertiesJPA;
	@Autowired
	private Hibernate propertiesHibernate;



	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		System.err.println(propertiesJPA.toString());
		System.err.println(propertiesHibernate.toString());

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(propertiesJPA.getPackagesToScan().toArray(new String[propertiesJPA.getPackagesToScan().size()]));
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties( additionalProperties() );

		return em;
	}

	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(propertiesJPA.getDatabase().getDriverClass());
		dataSource.setUrl(propertiesJPA.getDatabase().getUrl());
		dataSource.setUsername(propertiesJPA.getDatabase().getUsername());
		dataSource.setPassword(propertiesJPA.getDatabase().getPassword());

		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		System.out.println("Additional properties "+propertiesHibernate.toString());
		Properties properties = new Properties();
		
		properties.setProperty( "hibernate.default_catalog", propertiesHibernate.getDefault_catalog() );
		properties.setProperty( "hibernate.hbm2ddl.auto", propertiesHibernate.getHbm2ddl().getAuto() );
		properties.setProperty( "hibernate.dialect", propertiesHibernate.getDialect() );
		properties.setProperty( "hibernate.show_sql", propertiesHibernate.getShow_sql() );
		return properties;
	}

}