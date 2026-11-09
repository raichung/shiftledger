package com.raichung.shiftledger.common;

import com.raichung.shiftledger.user.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Who is making this request? Spring Security has already checked the password
// by the time our code runs, so we just read the logged-in user.
@Component
public class CurrentUser {

    public Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUser user) {
            return user.id();
        }
        throw new IllegalStateException("No logged-in user");
    }
}