package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@Service
public class UserAuthenticationService {
    @Autowired
    private UserService userService;

    public User getUser(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            Map details = (Map) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            String sub = (String) details.get("sub");
            return userService.findBySub(sub);
        } else {
            return userService.findByUsername(((User) authentication.getPrincipal()).getUsername());
        }
    }
    public Principal getPrincipal(Authentication authentication){
        if (authentication instanceof OAuth2Authentication) {
            return (Principal) ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal();
        } else {
            return (Principal) authentication.getPrincipal();
        }
    }
}
