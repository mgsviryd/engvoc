package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.service.UserService;
import by.sviryd.engvoc.service.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAlreadyExistsValidationService {
    @Autowired
    private UserService userService;

    public void validate(String username) throws UserAlreadyExistsException {
        if (userService.findByUsername(username) != null) {
            throw new UserAlreadyExistsException();
        }
    }
}
