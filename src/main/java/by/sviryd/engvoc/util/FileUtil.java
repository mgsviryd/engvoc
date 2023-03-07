package by.sviryd.engvoc.util;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static String getMimeType(File file) throws IOException {
        return new Tika().detect(file);
    }
}
