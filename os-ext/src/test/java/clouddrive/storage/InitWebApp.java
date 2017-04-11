package objectstack;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class StorageCorsConfiguration extends SpringBootServletInitializer implements WebApplicationInitializer, ApplicationContextInitializer<ConfigurableApplicationContext> {
	 @Override
	 public void onStartup(ServletContext container) {
	  AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
	  rootContext.register(RootConfiguration.class);
	  ContextLoaderListener contextLoaderListener = new ContextLoaderListener(rootContext);
	  container.addListener(contextLoaderListener);
	  container.setInitParameter("contextInitializerClasses", "mvctest.web.DemoApplicationContextInitializer");
	  AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
	  webContext.register(MvcConfiguration.class);
	  DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
	  ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", dispatcherServlet);
	  dispatcher.addMapping("/");
	 }
	 
	 @Override
	 public void initialize(ConfigurableApplicationContext ac) {
	  ConfigurableEnvironment appEnvironment = ac.getEnvironment();
	  appEnvironment.addActiveProfile("demo");

	 }
	 
	@Bean
	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
	    ServletRegistrationBean registration = new ServletRegistrationBean(
	            dispatcherServlet);
	    registration.addUrlMappings("/whatever/*", "/whatever2/*");
	    return registration;
	}
	
	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://domain1.com");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}

	@Bean
	@Primary
	public FilterRegistrationBean corsFilter1() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new StorageCorsFilter());
		bean.setOrder(0);
		return bean;
	}

}