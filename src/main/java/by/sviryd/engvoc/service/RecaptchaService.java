package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.RecaptchaConfig;
import by.sviryd.engvoc.domain.dto.RecaptchaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RecaptchaService {
    private final RecaptchaConfig recaptchaConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public RecaptchaService(RecaptchaConfig recaptchaConfig, RestTemplate restTemplate) {
        this.recaptchaConfig = recaptchaConfig;
        this.restTemplate = restTemplate;
    }

    public RecaptchaResponseDTO getCaptchaResponseDTO(String captchaResponse) {
        String url = String.format(recaptchaConfig.getUrl(), recaptchaConfig.getSecret(), captchaResponse);
        return restTemplate.postForObject(url, Collections.emptyList(), RecaptchaResponseDTO.class);
    }
}
