package by.sviryd.engvoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;


@SpringBootApplication
public class AttradeWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttradeWebApplication.class, args);
    }
}