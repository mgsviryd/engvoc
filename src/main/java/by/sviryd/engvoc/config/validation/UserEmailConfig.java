package by.sviryd.engvoc.config.validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:validation.properties")
@ConfigurationProperties(prefix = "registration.email")
public class UserEmailConfig {
    private String regex;
    private String message;
}
