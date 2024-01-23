package by.sviryd.engvoc.util;

import java.util.Locale;

public class LocaleUtils {
    public static Locale getLocaleByAbbr(String abbr) {
        if (abbr == null) return null;
        String[] split = abbr.split("_");
        if (split.length < 2) return null;
        return new Locale(split[0], split[1]);
    }
}
