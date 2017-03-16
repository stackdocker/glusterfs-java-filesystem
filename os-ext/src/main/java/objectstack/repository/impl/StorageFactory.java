package objectstack.repository.impl;

import javax.annotation.PostConstruct;

import objectstack.api.Message;
import objectstack.api.StorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class StorageFactory implements StorageService {
	private static final Logger logger = LoggerFactory
			.getLogger(StorageFactory.class);
    
	/*
	 * https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-logback
	 */
	@PostConstruct
	public void logSomething() {
		logger.debug(this.getClass().getSimpleName() + " created!");
	}

    /*
     * https://github.com/spring-guides/gs-uploading-files
     */
	public static class FileSystemStorageService implements StorageService {

	    private final Path rootLocation;

	    @Autowired
	    public FileSystemStorageService(StorageProperties properties) {
	        //this.rootLocation = Paths.get(properties.getLocation());
            this.rootLocation = Paths.get(properties.getVfs().getBase(), properties.getLocation());
	    }

	    @Override
	    public void store(MultipartFile file) {
	        try {
	            if (file.isEmpty()) {
	                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
	            }
	            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
	        } catch (IOException e) {
	            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
	        }
	    }

	    @Override
	    public Stream<Path> loadAll() {
	        try {
	            return Files.walk(this.rootLocation, 1)
	                    .filter(path -> !path.equals(this.rootLocation))
	                    .map(path -> this.rootLocation.relativize(path));
	        } catch (IOException e) {
	            throw new StorageException("Failed to read stored files", e);
	        }

	    }

	    @Override
	    public Path load(String filename) {
	        return rootLocation.resolve(filename);
	    }

	    @Override
	    public Resource loadAsResource(String filename) {
	        try {
	            Path file = load(filename);
	            Resource resource = new UrlResource(file.toUri());
	            if(resource.exists() || resource.isReadable()) {
	                return resource;
	            }
	            else {
	                throw new StorageFileNotFoundException("Could not read file: " + filename);

	            }
	        } catch (MalformedURLException e) {
	            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
	        }
	    }

	    @Override
	    public void deleteAll() {
	        FileSystemUtils.deleteRecursively(rootLocation.toFile());
	    }

	    @Override
	    public void init() {
	        try {
	            Files.createDirectory(rootLocation);
	        } catch (IOException e) {
	            throw new StorageException("Could not initialize storage", e);
	        }
	    }

	    @Override
	    public void storeAll(MultipartFile[] files) {
	        Arrays.stream(files).forEach(file -> store(file));
	    }
	}
	
	private final StorageService repos;
    
    @Autowired
    private ApplicationContext context;

    @Autowired
    public StorageFactory(StorageProperties properties) {
    	logger.debug("Glusterfs=" + (null == context ? "null" : context.toString()));
    	String scheme = properties.getFss();
    	if ( null == scheme || scheme.length() == 0 || scheme.toLowerCase() == "file" || scheme.toLowerCase() == "vfs" ) {
            this.repos = new FileSystemStorageService(properties);
    	} else {
    		this.repos = new Glusterfs();
    	}
    }

    @Override
    public void store(MultipartFile file) {
        this.repos.store(file);
    }

    @Override
    public void storeAll(MultipartFile[] files) {
        Arrays.stream(files).forEach(file -> this.repos.store(file));
    }

    @Override
    public Stream<Path> loadAll() {
        return this.repos.loadAll();
    }

    @Override
    public Path load(String filename) {
        return this.repos.load(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        return this.repos.loadAsResource(filename);
    }

    @Override
    public void deleteAll() {
    	this.repos.deleteAll();
    }
    
    @Override
    public void init() {
    	this.repos.init();
    }
}