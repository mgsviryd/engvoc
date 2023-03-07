package by.sviryd.engvoc.service.stringReplacementService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class URLKeysEntityFieldsStringReplacementService implements IStringReplacementService {
    private static final String REGEX = "-";

    @Override
    public String replace(String string) {
        String[] split = string.split(REGEX);
        StringBuilder sb = new StringBuilder();
        sb.append(split[0]);
        for (int i = 1; i < split.length; i++) {
            sb.append(StringUtils.capitalize(split[i]));
        }
        return sb.toString();
    }
}
