package syncstack;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class Application {
	ApplicationContext context;
	
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
				if (true != appConf.getAsvertxapp()) processSync();
				return RepeatStatus.FINISHED;
			}
			
			public void processSync() {
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

    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
    	SpringApplicationBuilder builder = new SpringApplicationBuilder(app.class);
    	ConfigurableApplicationContext ctx;
    	
    	Runnable httpRunner = () -> {

    		ctx = builder.web(true).run(args);
    		
    	}

    	new Thread(httpRunner).start();
    	
    	context = ctx
 
        ctx.application().dispatch();
    			
        context.close();
    }
    
    public void dispatch() {
    	//JobLauncher jobLauncher = ctx.getBean(MyJobLauncher.class);
    	//Job job = ctx.getBean(Job.class);
        //JobExecution jobExecution = jobLauncher.run(job,
    	//		new JobParametersBuilder()
    	//				.addString("hdfsSourceDirectory", "/data/analysis/results/part-*")
    	//				.addDate("date", new Date()).toJobParameters());
		
        MainJobLauncher main = ctx.getBean(MainJobLauncher.class);
        
        JobExecution jobExecution = main.jobLauncher.run(main.importUserJob, new JobParameters());
 
        MainHelper.reportResults(jobExecution);
        MainHelper.reportPeople(context.getBean(JdbcTemplate.class));
   	
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }

}