package by.sviryd.engvoc.provider;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.service.UserService;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuthProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        User user;
        if (GenericValidator.isEmail(username)) {
            user = userService.loadUserByEmail(username);
        } else {
            user = userService.loadUserByUsername(username);
        }
        if (user != null) {
            if (!userService.isMatchedPassword(password, user.getPassword())) {
                throw new BadCredentialsException("Wrong password!");
            }
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else throw new BadCredentialsException("Username not found!");
    }

    @Override
    public boolean supports(Class<?> arg) {
        return true;
    }
}