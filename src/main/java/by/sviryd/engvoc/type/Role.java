package by.sviryd.engvoc.type;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    WORKER;

    @Override
    public String getAuthority() {
        return name();
    }
}
