package by.sviryd.engvoc.config;

import by.sviryd.engvoc.loader.YamlPropertyLoaderFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;
import java.util.Locale;

@Getter
@Setter
@Configuration
@PropertySource(value = "classpath:jsoup.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "jsoup.s3.ru.product")
public class JsoupS3RuProductConfig {
    private URL url;
    private Locale locale;
    private boolean extractByFileLoading;
    private String defaultPictureFileName;
    private String nameParent;
    private String nameChild;
    private String codeParent;
    private String codeChild;
    private Integer codeIndexElement;
    private String categoriesParent;
    private String categoriesChild;
    private Integer categoriesStartWithIndex;
    private String propertiesParent;
    private String propertiesChild;
    private String imageParent;
    private String imageChild;
}
