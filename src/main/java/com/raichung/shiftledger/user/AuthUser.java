package com.raichung.shiftledger.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

// The logged-in user, as Spring Security sees them.
// We keep the id here so CurrentUser can read it straight away.
public record AuthUser(Long id, String email, String passwordHash) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // no roles yet, everyone is a normal user
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}