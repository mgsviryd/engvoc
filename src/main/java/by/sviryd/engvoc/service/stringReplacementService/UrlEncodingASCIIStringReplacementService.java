package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UrlEncodingASCIIStringReplacementService implements IStringReplacementService {
    private static final Map<String, String> letters = new HashMap<>();

    static {
        letters.put("&", "%26");
        letters.put("+", "%2B");
        letters.put("=", "%3D");
        letters.put("#", "%23");
        letters.put("~", "%7E");
    }

    @Override
    public String replace(String text) {
        if (text != null) {
            return IStringReplacementService.replace(letters, text);
        } else {
            return null;
        }
    }
}
