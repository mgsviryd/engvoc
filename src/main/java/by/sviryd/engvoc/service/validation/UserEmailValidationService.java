package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.config.validation.UserEmailConfig;
import by.sviryd.engvoc.service.exception.UserEmailValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEmailValidationService {
    @Autowired
    private UserEmailConfig config;

    public void validate(String password) throws UserEmailValidationException {
        if (!password.matches(config.getRegex())) {
            throw new UserEmailValidationException(config.getMessage());
        }
    }
}
