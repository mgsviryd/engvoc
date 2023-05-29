package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.service.exception.StringEmptyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StringEmptyValidationService {
    public void validate(String string) throws StringEmptyException {
        if (StringUtils.isEmpty(string)) {
            throw new StringEmptyException();
        }
    }
}
