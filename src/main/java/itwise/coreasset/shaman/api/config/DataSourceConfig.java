package itwise.coreasset.shaman.api.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author kkuru
 *
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig implements InitializingBean {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private Environment env;

	@Bean
	public DataSource getDataSource(){
		logger.info("Create DataSource");
		final BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
		dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
		dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
		
//		set validation query
		dataSource.setValidationQuery("select 1");
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(600000);
		
		return dataSource;
	}
	
	@Bean
	public DataSourceTransactionManager getTransactionManager(){
		return new DataSourceTransactionManager(getDataSource());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
