package objectstack;

/*
 * https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-batch
 */
import io.vertx.core.Vertx;
import javax.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableBatchProcessing
public class App implements ApplicationContextAware {

	@Autowired
    AppConf appConf;

	@Autowired
	private ServerVertx staticServer;

	@PostConstruct
	public void deployVerticle() {
		if (true != appConf.getAsvertxapp()) return;
	    Vertx.vertx().deployVerticle(staticServer);
	}	
	
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}
    
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	protected Tasklet tasklet() {

		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext context) {
				if (true != appConf.getAsvertxapp()) processUpload();
				return RepeatStatus.FINISHED;
			}
			
			public void processUpload() {
			    ApplicationContext context = applicationContext; /* new AnnotationConfigApplicationContext(AppConfig.class); */
			    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

			    MultipartFileUploader[] uploadTasks = new MultipartFileUploader[10];
			    for (int i = 0; i < 10; i++) {
			    	uploadTasks[i] = (MultipartFileUploader) context.getBean(MultipartFileUploader.class);
			        uploadTasks[i].setName("Thread 1");
			        taskExecutor.execute(uploadTasks[i]);
			    }

				for (;;) {
					int count = taskExecutor.getActiveCount();
					System.out.println("Active Threads : " + count);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (count == 0) {
						taskExecutor.shutdown();
						break;
					}
				}
			}
		};

	}

	@Bean
	public Job job() throws Exception {
		return this.jobs.get("job").start(step1()).build();
	}

	@Bean
	protected Step step1() throws Exception {
		return this.steps.get("step1").tasklet(tasklet()).build();
	}

	public static void main(String[] args) throws Exception {
		// System.exit is common for Batch applications since the exit code can be used to
		// drive a workflow
		System.exit(SpringApplication
				.exit(SpringApplication.run(App.class, args)));
	}

}