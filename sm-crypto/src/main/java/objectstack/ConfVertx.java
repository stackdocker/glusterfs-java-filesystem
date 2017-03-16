package objectstack;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Value;
import org.springframework.core.env.Environment;

/**
 * A configuration bean.
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@Configuration
public class ConfVertx {

  @Autowired
  Environment environment;

  int httpPort() {
    return environment.getProperty("http.port", Integer.class, 8080);
  }

  @Value("${swxa.hosts}")
  private String[] swxaHosts;
  
  @Value("${swxa.ports}")
  private Integer swxaPort;
  
}