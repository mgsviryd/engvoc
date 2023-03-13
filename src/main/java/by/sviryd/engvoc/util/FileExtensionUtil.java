package by.sviryd.engvoc.util;

public class FileExtensionUtil {
    private static final String EXCEL_SUFFIX = ".xlsx";
    private static final String XML_SUFFIX = ".xml";
    public static boolean isExcel(String name) {
        return name != null && name.endsWith(EXCEL_SUFFIX);
    }

    public static boolean isXml(String name) {
        return name != null && name.endsWith(XML_SUFFIX);
    }
}