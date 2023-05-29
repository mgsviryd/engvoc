package by.sviryd.engvoc.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserPasswordRepeatValidationException extends Exception {
    public UserPasswordRepeatValidationException(String message) {
        super(message);
    }
}
