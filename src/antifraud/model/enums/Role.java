package antifraud.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ANONYMOUS, MERCHANT, ADMINISTRATOR, SUPPORT;

    final String roleName = "ROLE_" + name();


    @Override
    public String getAuthority() {
        return roleName;
    }
}
