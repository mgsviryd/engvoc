package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.service.exception.UserEmailValidationException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

@Service
public class UserEmailValidationService {
    public void validate(String email) throws UserEmailValidationException {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new UserEmailValidationException();
        }
    }
}
