package by.sviryd.engvoc.config.card.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "excel.card.column")
public class ExcelCardColumnConfig {
    private int word;
    private int translation;
    private int workspace;
    private int example;
    private int exampleTranslation;
    private int dictionary;
    private int transcription;
    private int learned;
    private int sound;
    private int creationLDT;
    private int learnedLDT;
    private int forgotLDT;
    private int countForgot;
    private int picture;
    private int invisible;
}
