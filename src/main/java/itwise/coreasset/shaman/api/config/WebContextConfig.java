package itwise.coreasset.shaman.api.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created by chanwook on 2014. 7. 29..
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"itwise.coreasset.shaman.api"}, useDefaultFilters = false,
		includeFilters = {@ComponentScan.Filter(value = {Controller.class})})
public class WebContextConfig extends WebMvcConfigurerAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public WebContextConfig() {
		logger.info("====> initialize WebContextConfig");
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
		configurer.favorParameter(false);
		configurer.favorPathExtension(false);
		configurer.ignoreAcceptHeader(false);
	}

	@Bean
	public ViewResolver getCnvr() {
		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();

		// Setting to ViewResolver List
		ArrayList<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
		viewResolvers.add(new BeanNameViewResolver());
		viewResolver.setViewResolvers(viewResolvers);

		// Setting to Default View
		ArrayList<View> defaultViews = new ArrayList<View>();
		defaultViews.add(new MappingJackson2JsonView());
		viewResolver.setDefaultViews(defaultViews);

		return viewResolver;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		supportedMediaTypes.add(mediaType);
		messageConverter.setSupportedMediaTypes(supportedMediaTypes);
		converters.add(messageConverter);
	}
}
