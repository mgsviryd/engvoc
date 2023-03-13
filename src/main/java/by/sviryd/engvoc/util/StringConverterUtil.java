package by.sviryd.engvoc.util;

import java.time.LocalDateTime;

public class StringConverterUtil {
    public static String EMPTY = "";

    public static String getStringTrueOrEmpty(boolean bool) {
        return bool ? "true" : EMPTY;
    }

    public static String getTrimOfEmpty(String text) {
        if (text != null) {
            return text.trim();
        } else {
            return EMPTY;
        }
    }

    public static boolean getBoolean(String text) {
        return Boolean.parseBoolean(text);
    }

    public static LocalDateTime getLDTOrNull(String text) {
        try {
            return LocalDateTime.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getIntegerOrNull(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringOrEmpty(Object obj) {
        if (obj != null) {
            return obj.toString();
        } else {
            return EMPTY;
        }
    }
    public static String[] splitCamelCaseString(String s){
        if (s == null) return new String[0];
        return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }
    public static boolean isNotNullOrEmpty(String text){
        return text != null && !text.isEmpty();
    }
}
