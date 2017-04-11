package objectstack;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

@Configuration
public class InitWebApp extends SpringBootServletInitializer implements WebApplicationInitializer {

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(InitWebApp.class, MyConfiguration.class);
    }

    @Bean
    public MyServlet dispatcherServlet() {
        return new MyServlet();
    }

    @Bean
    public ServletRegistrationBean dispatcherServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet());
        Map<String,String> params = new HashMap<String,String>();
        params.put("org.atmosphere.servlet","org.springframework.web.servlet.DispatcherServlet");
        params.put("contextClass","org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
        params.put("contextConfigLocation","MyConfiguration");
        registration.setInitParameters(params);
        return registration;
    }
	 
	 @Override
	 public void onStartup(ServletContext container) {
	     AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
	     rootContext.register(RootConfiguration.class);
	     ContextLoaderListener contextLoaderListener = new ContextLoaderListener(rootContext);
	     container.addListener(contextLoaderListener);
	     container.setInitParameter("contextInitializerClasses", "clouddrive.InitAppCtx");
	     AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
	     webContext.register(MvcConfiguration.class);
	     DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
	     ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", dispatcherServlet);
	     dispatcher.addMapping("/");

	     
	     container.addFilter("Spring OpenEntityManagerInViewFilter", org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter.class)
	      .addMappingForUrlPatterns(null, false, "/*");
	    
	     container.addFilter("HttpMethodFilter", org.springframework.web.filter.HiddenHttpMethodFilter.class)
	      .addMappingForUrlPatterns(null, false, "/*");
	    
	     container.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"))
	        .addMappingForUrlPatterns(null, false, "/*");
	    
	     FilterRegistration charEncodingfilterReg = container.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class);
	     charEncodingfilterReg.setInitParameter("encoding", "UTF-8");
	     charEncodingfilterReg.setInitParameter("forceEncoding", "true");
	     charEncodingfilterReg.addMappingForUrlPatterns(null, false, "/*");	 
	 }

}