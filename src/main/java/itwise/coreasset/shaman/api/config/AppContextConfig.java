package itwise.coreasset.shaman.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan(useDefaultFilters = true,
        basePackages = {"itwise.coreasset.shaman.api"},
        excludeFilters = {@ComponentScan.Filter(value = {Controller.class, Configuration.class})})
@EnableTransactionManagement
public class AppContextConfig {

}
