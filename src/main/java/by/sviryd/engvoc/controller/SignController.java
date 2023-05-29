package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.config.RegistrationTokenExpirationDTConfig;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.VerificationToken;
import by.sviryd.engvoc.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/sign")
public class SignController {
    @Autowired
    public RegisterUserService registerUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Value("${info.url}")
    private String url;
    @Autowired
    private RegistrationTokenExpirationDTConfig registrationTokenExpirationDTConfig;
    @Autowired
    private LocaleExceptionAttributeWrapperService localeExceptionAttributeWrapperService;
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping("/up")
    public String up() {
        return "main";
    }

    @GetMapping("/in")
    public String in() {
        return "main";
    }

    @PostMapping(value = "/failure")
    public ModelAndView failure(
            HttpServletRequest request
    ) {
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:" + "/json/sign/failure");
    }

    @PostMapping(value = "/success")
    public ModelAndView success(
            HttpServletRequest request
    ) {
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:" + "/json/sign/success");
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
