package clouddrive;

import objectstack.api.MessageRepository;
import objectstack.api.StorageService;
//import objectstack.repository.impl.Cephfs;
import objectstack.repository.impl.InMemoryMessageRepository;
import objectstack.repository.impl.Glusterfs;
import objectstack.repository.impl.StorageProperties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App extends InitWebApp {
    
    public static void main(String[] args) {
         new SpringApplicationBuilder(App.class)
            .initializers(new InitAppCtx())
            .run(args);
    }

	@Bean
	CommandLineRunner init(App obj) {
		/*
		 * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner
		 * ... offer a single run method which will be called just before SpringApplication.run(…​) completes.
		 */
		return (args) -> {
			System.out.println(obj.toString());
		};
	}
}