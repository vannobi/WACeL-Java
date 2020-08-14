package pe.edu.unsa.daisi.lis.cel.config;

import static org.hibernate.cfg.AvailableSettings.C3P0_ACQUIRE_INCREMENT;
import static org.hibernate.cfg.AvailableSettings.C3P0_MAX_SIZE;
import static org.hibernate.cfg.AvailableSettings.C3P0_MAX_STATEMENTS;
import static org.hibernate.cfg.AvailableSettings.C3P0_MIN_SIZE;
import static org.hibernate.cfg.AvailableSettings.C3P0_TIMEOUT;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan("pe.edu.unsa.daisi.lis.cel.repository"),
		@ComponentScan("pe.edu.unsa.daisi.lis.cel.service"), @ComponentScan("pe.edu.unsa.daisi.lis.cel.security")})
public class AppJPAConfig {

	@Autowired
	private Environment env;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "pe.edu.unsa.daisi.lis.cel.domain.model"});

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		Properties props = new Properties();
		// Setting JDBC properties
	   
	   
	    // Setting JDBC properties
	    dataSource.setDriverClassName(env.getRequiredProperty("mysql.driver"));
		dataSource.setUrl(env.getRequiredProperty("mysql.jdbcUrl"));
		dataSource.setUsername(env.getRequiredProperty("mysql.username") );
		dataSource.setPassword(env.getRequiredProperty("mysql.password") );
		
		 // Setting Hibernate properties
	    props.put(SHOW_SQL, env.getRequiredProperty("hibernate.show_sql"));
	    props.put(HBM2DDL_AUTO, env.getRequiredProperty("hibernate.hbm2ddl.auto"));

	    // Setting C3P0 properties
	    props.put(C3P0_MIN_SIZE, env.getRequiredProperty("hibernate.c3p0.min_size"));
	    props.put(C3P0_MAX_SIZE, env.getRequiredProperty("hibernate.c3p0.max_size"));
	    props.put(C3P0_ACQUIRE_INCREMENT, env.getRequiredProperty("hibernate.c3p0.acquire_increment"));
	    props.put(C3P0_TIMEOUT, env.getRequiredProperty("hibernate.c3p0.timeout"));
	    props.put(C3P0_MAX_STATEMENTS, env.getRequiredProperty("hibernate.c3p0.max_statements"));
		
		dataSource.setConnectionProperties(props);
		
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto",env.getRequiredProperty("hibernate.hbm2ddl.auto")); //create-drop
		properties.setProperty("hibernate.dialect", env.getRequiredProperty("mysql.dialect"));

		return properties;
	}

}
