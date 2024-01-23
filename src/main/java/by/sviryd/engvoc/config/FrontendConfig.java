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
    @Autowired
    private AudioGenerateConfig audioGenerateConfig;

    private Map<Object, Object> config;
    private Map<Object, Object> version;

    @PostConstruct
    public void init(){
        config.put("uploadPicture", serverPathConfig.getUploadPicture());
        config.put("uploadAudio", serverPathConfig.getUploadAudio());
        config.put("staticPicture", serverPathConfig.getStaticPicture());
        config.put("audioGenerateLocales", audioGenerateConfig.getLocales());
    }
}
