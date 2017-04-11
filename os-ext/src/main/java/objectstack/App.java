package objectstack;

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
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by tangfeixiong (tangfx128@gmail.com) on 3/6/17.
 */
@SpringBootApplication
public class App {	
//    @Bean
//    public Glusterfs glusterfs() {
//        return new Glusterfs();
//    }
    
    @Bean
    public MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }   
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
	@Bean
	CommandLineRunner init(StorageService storageService) {
		/*
		 * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner
		 * ... offer a single run method which will be called just before SpringApplication.run(…​) completes.
		 */
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
	
	/*
	 * https://github.com/spring-guides/gs-rest-service-cors
	 */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/apis/**").allowedOrigins("http://localhost:9000")
    			    .allowedMethods("PUT", "DELETE")
    			    .allowedHeaders("header1", "header2", "header3")
    			    .exposedHeaders("header1", "header2")
    			    .allowCredentials(false).maxAge(3600);
                registry.addMapping("/storage").allowedOrigins("http://localhost:9000");
            }
        };
    }	
}
