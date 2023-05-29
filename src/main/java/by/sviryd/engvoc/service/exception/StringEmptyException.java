package by.sviryd.engvoc.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StringEmptyException extends Exception {
    public StringEmptyException(String message) {
        super(message);
    }
}
