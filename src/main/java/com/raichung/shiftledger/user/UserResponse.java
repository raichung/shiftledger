package com.raichung.shiftledger.user;

// No password hash here, ever
public record UserResponse(Long id, String email, String displayName) {

    static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName());
    }
}