package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.VerificationToken;
import by.sviryd.engvoc.service.SignUpUserService;
import by.sviryd.engvoc.service.UserService;
import by.sviryd.engvoc.service.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/sign")
public class SignController {
    @Autowired
    public SignUpUserService signUpUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenService verificationTokenService;

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
    public String activate(@PathVariable VerificationToken token) {
        if (token != null) {
            if (token.isExpiredToken()) {
                return "redirect:" + "/sign/activation/expiredToken";
            } else {
                User user = token.getUser();
                user.setToken("1Aa".concat(UUID.randomUUID().toString()));
                user.setActive(true);
                userService.save(user);
                verificationTokenService.delete(token);
                return "redirect:" + "/sign/activation/success";
            }
        } else {
            return "redirect:" + "/sign/activation/failure";
        }
    }

    @GetMapping("/activation/expiredToken")
    public String activationExpiredToken() {
        return "main";
    }

    @GetMapping("/activation/success")
    public String activationSuccess() {
        return "main";
    }

    @GetMapping("/activation/failure")
    public String activationFailure() {
        return "main";
    }
}
