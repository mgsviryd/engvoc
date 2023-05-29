package by.sviryd.engvoc.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserPasswordValidationException extends Exception {
    public UserPasswordValidationException(String message) {
        super(message);
    }
}
