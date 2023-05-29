package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.service.exception.UserPasswordRepeatValidationException;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordRepeatValidationService {
    public void validate(String password, String passwordRepeat) throws UserPasswordRepeatValidationException {
        if (!password.equals(passwordRepeat)) {
            throw new UserPasswordRepeatValidationException();
        }
    }
}
