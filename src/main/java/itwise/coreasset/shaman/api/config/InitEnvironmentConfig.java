package itwise.coreasset.shaman.api.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author kkuru
 *
 */
public class InitEnvironmentConfig implements ApplicationContextInitializer<ConfigurableApplicationContext>{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final static String [] DEFAULT_ACTIVE_PROFILES = {"default"};

	
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		
		try {
			if (context.getEnvironment().getActiveProfiles().length == 0) {
				context.getEnvironment().setActiveProfiles(DEFAULT_ACTIVE_PROFILES);
			}
			
			logger.info("Using set spring profiles.  profiles='{}'", context.getEnvironment().getActiveProfiles());
			
			ConfigurableEnvironment env = context.getEnvironment();
			
			env.setDefaultProfiles(DEFAULT_ACTIVE_PROFILES);
			
			
			List<Resource> resources = new ArrayList<>();
			
			resources.add(new ClassPathResource("config/db-" + context.getEnvironment().getActiveProfiles()[0] + ".properties"));
			
			
			PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
			propertiesFactory.setLocations(resources.toArray(new Resource[resources.size()]));
		
			MutablePropertySources propertySources = env.getPropertySources();
			propertiesFactory.afterPropertiesSet();
			propertySources.addLast(new PropertiesPropertySource("dbProp", propertiesFactory.getObject()));
		} catch (Exception err) {
			throw new ApplicationContextException("init env failed.", err);
		}
	}

}
