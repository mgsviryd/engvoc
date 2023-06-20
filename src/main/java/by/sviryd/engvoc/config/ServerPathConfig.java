package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "server.path")
public class ServerPathConfig {
    private boolean createDirectories;
    private String absolute;
    private String upload;
    private String uploadPicture;
    private String staticPicture;

    @PostConstruct
    public void init() throws IOException {
        if (createDirectories) {
            createDirectories(getAbsolute());
            createDirectories(getAbsolute() + upload);
            createDirectories(getAbsolute() + uploadPicture);
        }
    }

    public void createDirectories(String pathName) throws IOException {
        Path path = Paths.get(pathName);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public String getAbsolute() {
        return getClasspath() + absolute;
    }

    public String getClasspath() {
        return System.getProperty("user.dir");
    }
}
