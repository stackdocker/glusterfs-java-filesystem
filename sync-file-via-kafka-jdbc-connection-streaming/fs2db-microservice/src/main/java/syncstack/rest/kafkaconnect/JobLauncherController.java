package syncstack.rest.kafkaconnect;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @RequestMapping(value="/apis/v1/",
    		headers = "X-CSRF-Token=*",
    		method=RequestMethod.POST,
    		produces={"application/json"})
    public String handleJobLauncher(@RequestParam(value="project", required=true) String prj,
    		@RequestParam(value="package", required=false, defaultValue="") String pkg,
    		@RequestParam(value="supply", required=false, defaultValue="") String sup) throws Exception{
        JobExecution je = jobLauncher.run(job, new JobParameters());
        return "started";
    }

    @RequestMapping(value="/apis/v1/joblauncher",
    		headers = "X-CSRF-Token=*",
    		method=RequestMethod.POST,
    		produces={"application/json"})
    public String handleJobLauncher(@RequestParam(value="project", required=true) String prj,
    		@RequestParam(value="package", required=false, defaultValue="") String pkg,
    		@RequestParam(value="supply", required=false, defaultValue="") String sup) throws Exception{
        JobExecution je = jobLauncher.run(job, new JobParameters());
        return "started";
    }
}