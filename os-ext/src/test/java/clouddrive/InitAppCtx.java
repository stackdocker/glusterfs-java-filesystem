package objectstack;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class InitAppCtx implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	 @Override
	 public void initialize(ConfigurableApplicationContext ac) {
	     ConfigurableEnvironment appEnvironment = ac.getEnvironment();
	     appEnvironment.addActiveProfile("os");

	 }

}