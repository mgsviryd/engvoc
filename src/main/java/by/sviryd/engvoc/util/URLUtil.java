package by.sviryd.engvoc.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {
    public static URL getURLOrNull(String stringUrl){
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException e) {
            return null;
        }
    }
    public static boolean isValidURL(String url) {
        try{
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
