package objectstack.repository.impl;

/*
 * https://www.mkyong.com/spring-boot/spring-boot-configurationproperties-example/
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "the-object-storage-home";
    private String fss = "vfs";
    private Vfs vfs = new Vfs();
    
    public static class Vfs {
        private String base = "/tmp";

        //getters and setters
        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        @Override
        public String toString() {
            return base;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFss() {
        return fss;
    }

    public void setFss(String fss) {
        this.fss = fss;
    }

    public Vfs getVfs() {
        return vfs;
    }

    public void setVfs(Vfs fs) {
        this.vfs = vfs;
    }
}