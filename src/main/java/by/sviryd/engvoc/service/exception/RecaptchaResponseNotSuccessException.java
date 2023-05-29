package by.sviryd.engvoc.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecaptchaResponseNotSuccessException extends Exception {
    public RecaptchaResponseNotSuccessException(String message) {
        super(message);
    }
}
