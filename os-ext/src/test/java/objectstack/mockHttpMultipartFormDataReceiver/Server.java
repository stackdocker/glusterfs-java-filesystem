/*
* Author:
*    tangfeixiong <tangfx128@gmail.com>
*
* http://stackoverflow.com/questions/25699727/multipart-file-upload-spring-boot
* http://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically
*/

package objectstack.mockHttpMultipartFormDataReceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.*;
import java.util.Properties;

/**
 * Created by tangfeixiong on 3/6/17.
 * https://spring.io/guides/gs/uploading-files/
 * https://github.com/netgloo/spring-boot-samples/tree/master/spring-boot-file-upload-with-ajax/src/main/resources/static
 * http://www.mkyong.com/spring-boot/spring-boot-file-upload-example/
 */
@SpringBootApplication
@RestController
@RequestMapping("/")
public class Server {
	
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("5120MB");
        factory.setMaxRequestSize("5120MB");
        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(1000000);
        return multipartResolver;
    }
	
    @RequestMapping
    public ModelAndView list() {
        System.out.println("show upload form");
        return new ModelAndView();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "multipart/form-data" ,
                    produces = { "application/json", "application/xml" })
    public ResponseEntity<ThingRepresentation> submitFile(MultipartHttpServletRequest request,
        @PathVariable("domain") String domainParam, @RequestParam(value = "type") String thingTypeParam,
        @RequestParam("file") MultipartFile[] submissions) throws Exception ｛
									
	｝ // method uploadFile

    /**
	* https://docs.spring.io/spring/docs/1.2.x/javadoc-api/org/springframework/web/multipart/MultipartFile.html
	*/
	@RequestMapping(value = "/v1", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> uploadFiles(
	    @RequestParam("files") MultipartFile[] files) {

        Properties properties = new Properties();
        properties.load(Server.class.getClassLoader().getResourceAsStream("application.properties"));

        String vagrantBox = properties.getProperty("glusterfs.server");
        String volname = properties.getProperty("glusterfs.volume");

        System.out.println(getProvider("gluster").toString());

        String mountUri = "gluster://" + vagrantBox + ":" + volname + "/";
        String testUri = "gluster://" + vagrantBox + ":" + volname + "/ecp2";
        Path mountPath = Paths.get(new URI(mountUri));

        FileSystem fileSystem = FileSystems.newFileSystem(new URI(mountUri), null);
        FileStore store = fileSystem.getFileStores().iterator().next();
        System.out.println("TOTAL SPACE: " + store.getTotalSpace());
        System.out.println("USABLE SPACE: " + store.getUsableSpace());
        System.out.println("UNALLOCATED SPACE: " + store.getUnallocatedSpace());
        System.out.println(fileSystem.toString());
	
	    try {
	        // Handle the received file here
	        // ...
			for (int i = 0; i < files.length; i++) {
		        Path glusterPath = Paths.get(new URI(testUri + "/filename" + i ));
		        System.out.println(glusterPath.getClass());
		        System.out.println(glusterPath);
		        System.out.println(glusterPath.getFileSystem().toString());
		        fileSystem.provider().checkAccess(glusterPath, AccessMode.READ, AccessMode.WRITE);
		        System.out.println("Can read & write file");
		        try {
		            fileSystem.provider().checkAccess(glusterPath, AccessMode.EXECUTE);
		            System.out.println("Uh oh, file is executable, that's bad.");
		        } catch (AccessDeniedException e) {
		            System.out.println("Can't execute file, that's good.");
		        }

				InputStream in = files[i].GetInputStream();
			    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(glusterPath, CREATE, APPEND))) {
		            while ((int c = in.read()) != -1) {
		                out.write(c);
		            }
			    } catch (IOException x) {
			      System.err.println(x);
			    } finally {
		            if (in != null) {
		                in.close();
		            }
		            if (out != null) {
		                out.close();
		            }
		        } 
			}
	    } catch (Exception e) {
			fileSystem.close();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
		
		fileSystem.close();
	    return new ResponseEntity<>(HttpStatus.OK);
	} // method uploadFiles
										
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
