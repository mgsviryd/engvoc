package by.sviryd.engvoc.config;

import by.sviryd.engvoc.type.LangLocale;
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
@ConfigurationProperties(prefix = "lang")
public class LocaleConfig {
    private String defaultAbbr;
    private List<String> localeAbbrs;
    private LangLocale defaultLangLocale;
    private List<LangLocale> langLocales;

    public boolean isPresent(LangLocale langLocale) {
        return langLocales.contains(langLocale);
    }
    @PostConstruct
    public void init(){
        defaultLangLocale = LangLocale.getLangLocale(defaultAbbr);
        langLocales = localeAbbrs.stream().map(LangLocale::getLangLocale).collect(Collectors.toList());
    }
}
