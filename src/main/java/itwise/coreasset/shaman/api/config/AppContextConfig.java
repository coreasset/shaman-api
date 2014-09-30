package itwise.coreasset.shaman.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;


@Configuration
@ComponentScan(useDefaultFilters = true,
		basePackages = {"itwise.coreasset.shaman.api"},
		excludeFilters = {@ComponentScan.Filter(value = {Controller.class, Configuration.class})})
@Import(value = {DataSourceConfig.class, MyBatisConfig.class})
public class AppContextConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public AppContextConfig() {
		logger.info("===> initialize AppContextConfig");
	}
	
}
