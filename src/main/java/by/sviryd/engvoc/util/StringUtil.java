package by.sviryd.engvoc.util;

import org.springframework.util.StringUtils;

public class StringUtil {
    public static String trimIfNotNull(String text) {
        if (text != null) {
            text = text.trim();
        }
        return text;
    }

    public static String renameToNullIfContains(String text, String matcher) {
        if (text.contains(matcher)) {
            return null;
        } else {
            return text;
        }
    }

    public static String removeSpaces(String text) {
        if (text != null) {
            return text.replaceAll("\\s+", "");
        } else {
            return text;
        }
    }
    public static String getFilenameWithoutExtension(String filename){
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null) return null;
        return filename.substring(0,filename.indexOf(extension)-1);
    }
}
