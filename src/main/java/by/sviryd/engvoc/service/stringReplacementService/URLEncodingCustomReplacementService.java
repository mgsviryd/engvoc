package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

@Service
public class URLEncodingCustomReplacementService implements IStringReplacementService {
    @Override
    public String replace(String text) {
        return UriUtils.encodePath(text, "UTF-8");
    }
}
