package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "hibernate.search")
public class HibernateSearchConfig {
    private boolean init;
    private boolean purgeAllOnStart;
    private boolean optimizeOnFinish;
    private Integer batchSizeToLoadObjects;
    private Integer threadsToLoadObjects;
    private String regexAnyChar;
    private Integer minGramSize;
}
