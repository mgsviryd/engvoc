package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;

@Service
public class UpperCaseLowerCaseStringReplacementService implements IStringReplacementService {
    @Override
    public String replace(String string) {
        return string.toLowerCase();
    }
}
