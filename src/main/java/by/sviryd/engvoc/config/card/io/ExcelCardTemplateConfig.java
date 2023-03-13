package by.sviryd.engvoc.config.card.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "excel.card.template")
public class ExcelCardTemplateConfig {
    private String shortTemplateFilename;
    private String fullTemplateFilename;
}