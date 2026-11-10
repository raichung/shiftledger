package com.raichung.shiftledger.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

@Service
public class UserService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserService(AppUserRepository users, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, TokenService tokenService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    public UserResponse signUp(SignupRequest req) {
        // "Ray@Example.com " and "ray@example.com" are the same person
        String email = req.email().trim().toLowerCase(Locale.ROOT);

        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        // Scramble the password one way. We can check it later, but never read it back.
        String hash = passwordEncoder.encode(req.password());

        AppUser saved = users.save(new AppUser(email, hash, req.displayName().trim()));
        return UserResponse.from(saved);
    }

    public TokenResponse login(LoginRequest req) {
        try {
            // Spring Security finds the user and checks the password against the hash
            Authentication auth = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(req.email(), req.password()));
            return tokenService.issue((AuthUser) auth.getPrincipal());
        } catch (AuthenticationException e) {
            // Same answer for a wrong email or a wrong password
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        return users.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}