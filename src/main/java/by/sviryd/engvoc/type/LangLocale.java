package by.sviryd.engvoc.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.Locale;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LangLocale {
    en_US("en", "US", 1033),
    ru_RU("ru", "RU", 1049);

    private final String lang;
    private final String country;
    private final Integer id;

    LangLocale(String lang, String country, Integer id) {
        this.lang = lang;
        this.country = country;
        this.id = id;
    }

    public static LangLocale getLangLocale(String langCountry) {
        if (StringUtils.isEmpty(langCountry)) return null;
        try {
            return valueOf(langCountry);
        } catch (Exception e) {
            return null;
        }
    }

    public Locale getLocale() {
        return new Locale(lang, country);
    }

    public String getLang() {
        return lang;
    }

    public String getCountry() {
        return country;
    }

    public Integer getId() {
        return id;
    }
}
