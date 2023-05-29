package by.sviryd.engvoc.service.validation;

import by.sviryd.engvoc.domain.dto.RecaptchaResponseDTO;
import by.sviryd.engvoc.service.RecaptchaService;
import by.sviryd.engvoc.service.exception.RecaptchaResponseNotSuccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaResponseValidationService {
    @Autowired
    private RecaptchaService recaptchaService;

    public void validate(String recaptchaResponse) throws RecaptchaResponseNotSuccessException {
        RecaptchaResponseDTO response = recaptchaService.getCaptchaResponseDTO(recaptchaResponse);
        if (!response.isSuccess()) {
            throw new RecaptchaResponseNotSuccessException();
        }
    }
}
