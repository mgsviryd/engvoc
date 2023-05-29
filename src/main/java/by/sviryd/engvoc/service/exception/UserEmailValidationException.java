package by.sviryd.engvoc.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserEmailValidationException extends Exception {
    public UserEmailValidationException(String message) {
        super(message);
    }
}
