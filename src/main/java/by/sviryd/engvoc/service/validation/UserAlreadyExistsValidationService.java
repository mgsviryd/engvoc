package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.service.UserService;
import by.sviryd.engvoc.service.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAlreadyExistsValidationService {
    @Autowired
    private UserService userService;

    public void validate(String email) throws UserAlreadyExistsException {
        if (userService.findByEmail(email) != null) {
            throw new UserAlreadyExistsException();
        }
    }
}
