package by.sviryd.engvoc.config;

import by.sviryd.engvoc.util.LocaleUtils;
import lombok.Getter;
import lombok.Setter;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "audio.generate")
public class AudioGenerateConfig {
    private List<String> localeAbbrs;
    private List<Locale> locales;
    private String defaultLocale;
    private boolean initMary;

    public boolean isPresent(Locale locale) {
        return locales.contains(locale);
    }

    public boolean isPresent(String localeAbbr) {
        return localeAbbrs.contains(localeAbbr);
    }

    @PostConstruct
    public void init() throws MaryConfigurationException {
        if(initMary){
            new LocalMaryInterface(); // init mary
        }
        locales = localeAbbrs.stream().map(LocaleUtils::getLocaleByAbbr).collect(Collectors.toList());
    }
}
