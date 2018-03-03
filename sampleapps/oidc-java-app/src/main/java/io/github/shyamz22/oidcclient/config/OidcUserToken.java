package io.github.shyamz22.oidcclient.config;

import io.github.shyamz.openidconnect.response.model.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class OidcUserToken implements Authentication, Serializable {

    private final AuthenticatedUser authenticatedUser;
    private boolean isAuthenticationSuccess;

    public OidcUserToken(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.isAuthenticationSuccess = authenticatedUser != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return authenticatedUser.getTokens();
    }

    @Override
    public Object getDetails() {
        return authenticatedUser.getClientInfo();
    }

    @Override
    public Object getPrincipal() {
        return authenticatedUser.getUserInfo();
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticationSuccess;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        isAuthenticationSuccess = isAuthenticated;
    }

    @Override
    public String getName() {
        if (Objects.nonNull(authenticatedUser.getUserInfo().getEmail())) {
            return authenticatedUser.getUserInfo().getEmail().getEmail();
        }
        return "";
    }
}
