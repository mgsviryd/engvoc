package by.sviryd.engvoc.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Lang {
    EN("en", 1049),
    RU("ru", 1033);

    private final String name;
    private final Integer id;

    Lang(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public static Lang getLang(String caseInsensitive) {
        if (StringUtils.isEmpty(caseInsensitive)) return null;
        return valueOf(caseInsensitive.toUpperCase());
    }

    public static boolean isLanguage(String caseInsensitive) {
        return getLang(caseInsensitive) != null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
