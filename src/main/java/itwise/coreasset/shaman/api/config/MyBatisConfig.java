package itwise.coreasset.shaman.api.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author kkuru
 *
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "itwise.coreasset.shaman.api.mapper")
public class MyBatisConfig {

	@Bean
	public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, ApplicationContext context) throws IOException{
		
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		
		factoryBean.setDataSource(dataSource);
		
		factoryBean.setConfigLocation(context.getResource("classpath:config/mybatis-config.xml"));
		
		factoryBean.setTypeAliasesPackage("itwise.coreasset.shaman.api.model");
		
		factoryBean.setMapperLocations(context.getResources("classpath:config/*-mapper.xml"));
		
		return factoryBean;
	}
	
	
	@Bean(destroyMethod="clearCache")
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory){
		return new SqlSessionTemplate(sessionFactory);
	}
	
}
