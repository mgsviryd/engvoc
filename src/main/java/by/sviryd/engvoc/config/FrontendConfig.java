package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "frontend")
@Import(ServerPathConfig.class)
public class FrontendConfig {
    @Autowired
    private ServerPathConfig serverPathConfig;
    @Autowired
    private InfoConfig infoConfig;
    private Map<String, String> config;
    private Map<String, String> version;

    public String getCertainConfig(String name){
        return config.get(name);
    }
    public String getCertainVersion(String name){
        return version.get(name);
    }

    @PostConstruct
    public void init(){
        config.put("uploadPicture", serverPathConfig.getUploadPicture());
        config.put("staticPicture", serverPathConfig.getStaticPicture());
        config.put("url",infoConfig.getUrl());
        config.put("logo",infoConfig.getLogo());
    }
}
