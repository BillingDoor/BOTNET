package cs.sii.config.mvc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import cs.sii.model.RoleToUserProfileConverter;

//@Component
//public class CustomizedRestMvcConfiguration extends RepositoryRestConfigurerAdapter {
//
//  @Override
//  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//    configuration.setBasePath("/api")
//  }
//}


@Configuration
public class MvcConfig {
    
//	@Override
//  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//    config.setBasePath("/api");
//  }
	
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/hello").setViewName("hello");
//        registry.addViewController("/login").setViewName("login");
//    }
//	
//	@Autowired
//	RoleToUserProfileConverter roleToUserProfileConverter;
//
//	
//	/**
//	 * Aggiunta di un view controller che al path / associa la view index
//	 */
//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/").setViewName("test");
//	}
//	
//	/**
//     * Configure ResourceHandlers to serve static resources like CSS/ Javascript etc...
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/templates/**").addResourceLocations("/templates/");
//    }
//	
//	/**
//     * Configure ViewResolvers to deliver preferred views.
//     */
//	@Override
//	public void configureViewResolvers(ViewResolverRegistry registry) {
//
//		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//		viewResolver.setViewClass(JstlView.class);
//		viewResolver.setPrefix("classpath:/templates/");
//		viewResolver.setSuffix(".jsp");
//		registry.viewResolver(viewResolver);
//	}
//	
//    
//    /**
//     * Configure Converter to be used.
//     * In our example, we need a converter to convert string values[Roles] to UserProfiles in newUser.jsp
//     */
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(roleToUserProfileConverter);
//    }
//	
//
//    /**
//     * Configure MessageSource to lookup any validation/error message in internationalized property files
//     */
//    @Bean
//	public MessageSource messageSource() {
//	    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//	    messageSource.setBasename("messages");
//	    return messageSource;
//	}
//    
//    /**Optional. It's only required when handling '.' in @PathVariables which otherwise ignore everything after last '.' in @PathVaidables argument.
//     * It's a known bug in Spring [https://jira.spring.io/browse/SPR-6164], still present in Spring 4.1.7.
//     * This is a workaround for this issue.
//     */
//    @Override
//    public void configurePathMatch(PathMatchConfigurer matcher) {
//        matcher.setUseRegisteredSuffixPatternMatch(true);
//    }	
//	
	
}
