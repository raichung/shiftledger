package com.raichung.shiftledger.user;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}