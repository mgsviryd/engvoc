package by.sviryd.engvoc.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocaleExceptionMessage {
    private String code;
    private String attrName;
    private String message;
}
