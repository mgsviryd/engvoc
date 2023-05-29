package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.service.exception.UserAlreadyExistsException;
import by.sviryd.engvoc.service.validation.*;
import by.sviryd.engvoc.type.Role;
import by.sviryd.engvoc.util.LocaleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RegisterUserService {
    @Autowired
    private UserService userService;
    @Autowired
    private StringEmptyValidationService stringEmptyValidationService;
    @Autowired
    private UserAlreadyExistsValidationService userAlreadyExistsValidationService;
    @Autowired
    private UserEmailValidationService userEmailValidationService;
    @Autowired
    private UserPasswordValidationService userPasswordValidationService;
    @Autowired
    private UserPasswordRepeatValidationService userPasswordRepeatValidationService;
    @Autowired
    private RecaptchaResponseValidationService recaptchaResponseValidationService;
    @Autowired
    private LocaleExceptionWrapperService localeExceptionWrapperService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<LocaleException> register(User user) {
        List<LocaleException> exs = new ArrayList<>();
        String email = user.getEmail();
        User userFromDB = userService.findByEmail(email);
        if (userFromDB != null) {
            exs.add(new LocaleException(new UserAlreadyExistsException("Аккаунт " + email + " уже существует!"), email));
        }
        localeExceptionWrapperService.runAndWrap(() -> userEmailValidationService.validate(email), exs);
        localeExceptionWrapperService.runAndWrap(() -> userPasswordValidationService.validate(user.getPassword()), exs);
        if (exs.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(Role.USER));
            userService.save(user);
        }
        return exs;
    }

    public List<LocaleException> validate(String email, String password, String passwordRepeat, String recaptchaResponse) {
        List<LocaleException> exs = new ArrayList<>();
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> stringEmptyValidationService.validate(email), exs, "email");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> userAlreadyExistsValidationService.validate(email), exs, "email", email);
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> userEmailValidationService.validate(email), exs, "email", email);
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> stringEmptyValidationService.validate(password), exs, "password");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> userPasswordValidationService.validate(password), exs, "password");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> stringEmptyValidationService.validate(passwordRepeat), exs, "passwordRepeat");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> userPasswordRepeatValidationService.validate(password, passwordRepeat), exs, "passwordRepeat");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> stringEmptyValidationService.validate(recaptchaResponse), exs, "recaptcha");
        localeExceptionWrapperService.runAndWrapIfAttrNoPrevious(() -> recaptchaResponseValidationService.validate(recaptchaResponse), exs, "recaptcha");
        return exs;
    }
    public User register(String email, String password) {
            User user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(Collections.singleton(Role.USER));
            return userService.save(user);
    }
}
