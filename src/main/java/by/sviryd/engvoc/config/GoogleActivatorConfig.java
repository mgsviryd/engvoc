package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "google.activator")
@Import(ServerPathConfig.class)
public class GoogleActivatorConfig {
    @Autowired
    private ServerPathConfig serverPathConfig;

    private boolean init;
    private String projectId;
    private String defaultBucket;
    private String pathToJsonKey;

    public String getPathToJsonKey(){
        return serverPathConfig.getClasspath() + pathToJsonKey;
    }
}
