package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UrlReservedWordsCommonStringReplacementService implements IStringReplacementService {
    private static final String THE = "the-";
    private static final List RESERVED = Arrays.asList(
            "page",
            "size",
            "direction",
            "property",
            "is-need-property-choices"
    );

    @Override
    public String replace(String word) {
        if (word != null) {
            if (RESERVED.contains(word.toLowerCase())) {
                return THE + word;
            } else {
                return word;
            }
        } else {
            return null;
        }
    }
}
