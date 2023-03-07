package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;

@Service
public class SpaceHyphenStringReplacementService implements IStringReplacementService {
    private static final String REGEX_MARKS = "[\\s]";
    private static final String REPLACEMENT = "-";
    @Override
    public String replace(String string) {
        return string.replaceAll(REGEX_MARKS, REPLACEMENT);
    }
}
