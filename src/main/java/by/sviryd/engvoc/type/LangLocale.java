package by.sviryd.engvoc.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LangLocale {
    en_US("en", "US", "en_US", "1033"),
    ru_RU("ru", "RU", "ru_RU", "1049");

    private final String lang;
    private final String country;
    private final String locale;
    private final String id;

    LangLocale(String lang, String country, String locale, String id) {
        this.lang = lang;
        this.country = country;
        this.locale = locale;
        this.id = id;
    }
    @JsonCreator
    static LangLocale findValue(@JsonProperty("lang") String lang,
                                @JsonProperty("country") String country,
                                @JsonProperty("locale") String locale,
                                @JsonProperty("id") String id
                                ) {
        return Arrays.stream(LangLocale.values()).filter(x -> x.lang.equals(lang) && x.country.equals(country) && x.locale.equals(locale) && x.id.equals(id)).findFirst().orElse(null);
    }

    public static LangLocale findLangLocale(String lang_Country) {
        if (StringUtils.isEmpty(lang_Country)) return null;
        try {
            return valueOf(lang_Country);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isLangExist(String lang) {
        if (StringUtils.isEmpty(lang)) return false;
        return Arrays.stream(values()).anyMatch(x->x.getLang().toLowerCase().equals(lang));
    }

    public static LangLocale getByLangAndLocale(String lang, String country){
        lang = lang.toLowerCase();
        country = country.toUpperCase();
        String finalLang = lang;
        String finalCountry = country;
        return Arrays.stream(values()).filter(x->x.getLang().equals(finalLang) && x.getCountry().equals(finalCountry)).findFirst().orElse(null);
    }

    public static LangLocale findLangLocale(String lang, String country) {
        if (StringUtils.isEmpty(lang) || StringUtils.isEmpty(country)) return null;
        String lang_Country = format(lang, country);
        try {
            return valueOf(lang_Country);
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    private static String format(String lang, String country) {
        return lang.toLowerCase() + "_" + country.toUpperCase();
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

    public String getId() {
        return id;
    }
}
