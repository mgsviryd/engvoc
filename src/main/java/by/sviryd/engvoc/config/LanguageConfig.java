package by.sviryd.engvoc.config;

import by.sviryd.engvoc.domain.LanguageDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "language")
public class LanguageConfig {
    private List<String> abbrs;
    private List<String> names;

    public List<LanguageDTO> getLanguages() {
        List<LanguageDTO> languages = new ArrayList<>();
        int size = abbrs.size();
        for (int i = 0; i < size; i++) {
            languages.add(new LanguageDTO(
                    abbrs.get(i),
                    names.get(i)
            ));
        }
        return languages;
    }
}
