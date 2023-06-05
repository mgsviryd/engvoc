package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.repos.UserRepo;
import by.sviryd.engvoc.type.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public boolean isMatchedPassword(String password, String password2) {
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
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
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

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
