package by.sviryd.engvoc.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocaleException {
    @JsonIgnore
    private Exception exception;
    @JsonIgnore
    private Object[] args;
    private String code;
    private String attrName;

    public LocaleException(Exception exception, Object... args) {
        this.exception = exception;
        this.args = args;
        this.code = exception.getClass().getName();
        this.attrName = exception.getClass().getSimpleName();
    }

    public LocaleException(Exception exception, String message, String attrName, Object... args) {
        this.exception = exception;
        this.args = args;
        this.code = exception.getClass().getName();
        this.attrName = attrName;
    }
}
