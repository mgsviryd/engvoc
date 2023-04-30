package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.config.RegistrationTokenExpirationDTConfig;
import by.sviryd.engvoc.controller.utils.ControllerUtils;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.VerificationToken;
import by.sviryd.engvoc.domain.dto.RecaptchaResponseDTO;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.util.LocaleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;
    private final RecaptchaService recaptchaService;
    private final MailSenderService mailSenderService;
    private final VerificationTokenService verificationTokenService;
    @Value("${info.url}")
    private String url;

    @Autowired
    private RegistrationTokenExpirationDTConfig registrationTokenExpirationDTConfig;
    @Autowired
    private LocaleExceptionAttributeWrapperService localeExceptionAttributeWrapperService;
    @Autowired
    private MessageSourceOnlyLanguageService messageSourceOnlyLanguageService;

    @Autowired
    public RegistrationController(UserService userService, RecaptchaService recaptchaService, MailSenderService mailSenderService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.recaptchaService = recaptchaService;
        this.mailSenderService = mailSenderService;
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping
    public String registration(Model model, Locale locale) {
        String userPasswordInfo = messageSourceOnlyLanguageService.getMessage("userPasswordInfo", null, locale);
        model.addAttribute("user", new User());
        model.addAttribute("userPasswordInfo", userPasswordInfo);
        return "registration";
    }

    @PostMapping
    public String addUser(@RequestParam String passwordConfirm,
                          @RequestParam("g-recaptcha-response") String captchaResponse,
                          @Valid User user,
                          Locale locale,
                          BindingResult bindingResult,
                          Model model) {
        RecaptchaResponseDTO response = recaptchaService.getCaptchaResponseDTO(captchaResponse);
        if (!response.isSuccess()) {
            log.debug("Recaptcha error: {}", response);
            String captchaError = messageSourceOnlyLanguageService.getMessage("captchaError", null, locale);
            model.addAttribute("captchaError", captchaError);
        }
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            String passwordError = messageSourceOnlyLanguageService.getMessage("passwordError", null, locale);
            model.addAttribute("passwordError", passwordError);
        }
        if (!response.isSuccess() || bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
        }
        user.setUsername(user.getEmail());

        List<LocaleException> localeExceptions = userService.register(user);
        localeExceptionAttributeWrapperService.wrapAsAttributes(localeExceptions, model, locale);
        if (!localeExceptions.isEmpty()) {
            return "registration";
        }

        sendVerificationToken(user, locale);
        model.addAttribute("status", "success");
        return "registration";
    }

    private void sendVerificationToken(User user, Locale locale) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plus(registrationTokenExpirationDTConfig.getExpirationDT()));
        verificationTokenService.save(verificationToken);
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = MessageFormat.format(
                    "Приветствуем Вас, {0}! \n" +
                            "Вы зарегистрировались на " + url + "\n" +
                            "\n" +
                            "Перейдите по ссылке для подтверждения регистрации:\n " +
                            url + "/registration/activate/{1}",
                    user.getUsername(),
                    verificationToken.getToken()
            );
            mailSenderService.send(user.getEmail(), "Активация аккаунта", message);
        }
    }

    @GetMapping("/activate/{token}")
    public String activate(Model model, @PathVariable VerificationToken token) {
        if (token != null) {
            User user = token.getUser();
            user.setToken("1Aa".concat(UUID.randomUUID().toString()));
            user.setActive(true);
            userService.save(user);
            verificationTokenService.delete(token);
            return "redirect:/registration/activationSuccess";
        }
        model.addAttribute("activationError", "Введен неверный код!");
        return "activation";
    }
}
