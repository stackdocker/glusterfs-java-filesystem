package syncstack.biz;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder; 
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher; 
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

@Component
class MyJobLauncher extends SimpleJobLauncher implements JobLauncher {
	
	public JobExecution run(Job job, JobParameters jobParameters)
			throws JobExecutionAlreadyRunningException,
                    JobRestartException,
                    JobInstanceAlreadyCompleteException,
                    JobParametersInvalidException {
		super.run(job, jobParameters);
	}
	
	public void runSample(Job job) {
		this.run(job, 
		    new JobParametersBuilder() 
		      .addString("hdfsSourceDirectory", "/data/analysis/results/part-*") 
		      .addDate("date", new Date()).toJobParameters());
	}
}