package by.sviryd.engvoc.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

@ToString
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RecaptchaResponseDTO {
    private boolean success = true;
    @JsonAlias("error-codes")
    private Set<String> errorCodes = Collections.emptySet();
}
