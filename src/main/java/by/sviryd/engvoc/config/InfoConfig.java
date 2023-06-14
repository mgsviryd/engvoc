package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "info")
public class InfoConfig {
    private String url;
    private String contactUrl;
    private String logo;
    private String logoUrl;

    @PostConstruct
    private void postConstruct() {
        contactUrl = url + contactUrl;
        logoUrl = url + logoUrl;
    }
}
