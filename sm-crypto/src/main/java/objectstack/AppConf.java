package objectstack;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Value;
import org.springframework.core.env.Environment;

/**
 * A configuration bean.
 * 
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@Configuration
@PropertySource("classpath:/resources/app.properties")
public class AppConf {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	Environment environment;

	int httpPort() {
		return environment.getProperty("http.port", Integer.class, 8080);
	}
	
	@Value("${swxa.inifile:classpath:/resources/swsds.ini}")
	private String swxaInifile;
	
	@Value("${swxa.eccindex:3}")
	private int swxaEccindex;

	@Value("${swxa.hosts:127.0.0.1}")
	private String[] swxaHosts;

	@Value("${swxa.port:8008}")
	private int swxaPort;

	@Value("${asvertxapp:false}")
	private boolean asvertxapp;

}