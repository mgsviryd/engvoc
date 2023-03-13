package by.sviryd.engvoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "abbyy.splitter.latin.cyrillic")
public class AbbyyLatinCyrillicSplitterConfig {
    private String suffix_1;
    private String suffix_2;
    private String suffix_3;
    private String empty;
}
