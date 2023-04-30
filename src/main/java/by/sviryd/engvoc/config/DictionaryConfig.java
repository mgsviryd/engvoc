package by.sviryd.engvoc.config;

import by.sviryd.engvoc.type.Lang;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dictionary")
public class DictionaryConfig {
    private List<String> langAbbrs;
    private List<Lang> langs;

    public boolean isPresent(Lang lang){
        return langs.contains(lang);
    }

    @PostConstruct
    public void init(){
        langs = langAbbrs.stream().map(Lang::getLang).collect(Collectors.toList());
    }
}
