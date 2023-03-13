package by.sviryd.engvoc.config.card.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "xml.card.io.tag")
public class XmlCardIOTagConfig {
    private String card;
    private String word;
    private String meanings;
    private String meaning;
    private String statistics;
    private String status;
    private String sound;
    private String name;
    private int learnedMark;
    private String translations;
    private String transcription;
    private String examples;
    private String example;
}
