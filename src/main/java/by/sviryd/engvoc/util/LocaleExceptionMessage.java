package by.sviryd.engvoc.util;

import by.sviryd.engvoc.domain.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocaleExceptionMessage {
    @JsonView(Views.Code.class)
    private String code;
    @JsonView(Views.Attribute.class)
    private String attribute;
    @JsonView(Views.Message.class)
    private String message;
}
