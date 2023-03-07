package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.repos.UserRepo;
import by.sviryd.engvoc.service.exception.UserAlreadyExistsException;
import by.sviryd.engvoc.service.validation.UserEmailValidationService;
import by.sviryd.engvoc.service.validation.UserPasswordValidationService;
import by.sviryd.engvoc.type.Role;
import by.sviryd.engvoc.util.LocaleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserPasswordValidationService userPasswordValidationService;
    @Autowired
    private UserEmailValidationService userEmailValidationService;
    @Autowired
    private LocaleExceptionWrapperService localeExceptionWrapperService;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("User is not found.");
        }
        return user;
    }

    public boolean isMatchedPassword(String password, String password2){
        return passwordEncoder.matches(password, password2);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User loadUserByEmail(String s) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(s);
        if (user == null) {
            throw new UsernameNotFoundException("User is not found.");
        }
        return user;
    }

    public User findBySub(String sub) {
        return userRepo.findBySub(sub);
    }

    public List<LocaleException> register(User user) {
        List<LocaleException> exs = new ArrayList<>();
        String email = user.getEmail();
        User userFromDB = userRepo.findByEmail(email);
        if (userFromDB != null) {
            exs.add(new LocaleException(new UserAlreadyExistsException("Аккаунт " + email + " уже существует!"), email));
        }
        localeExceptionWrapperService.runAndWrap(() -> userEmailValidationService.validate(email), exs);
        localeExceptionWrapperService.runAndWrap(() -> userPasswordValidationService.validate(user.getPassword()), exs);
        if (exs.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(Role.USER));
            userRepo.save(user);
        }
        return exs;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = (email != null && !email.equals(userEmail)) || (userEmail != null && !userEmail.equals(email));
        if (isEmailChanged) {
            user.setEmail(email);
        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        userRepo.save(user);
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        userRepo.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        userRepo.save(user);
    }

    public Iterable<User> getUsersByTokens(List<String> tokens) {
        return userRepo.findAllByToken(tokens);
    }
}
