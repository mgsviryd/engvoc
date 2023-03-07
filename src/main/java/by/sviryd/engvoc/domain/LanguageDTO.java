package by.sviryd.engvoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import lombok.Value;

@ToString
@Value
@JsonIgnoreProperties(ignoreUnknown = true, value = {})
public class LanguageDTO {
    private String abbr;
    private String name;
}
