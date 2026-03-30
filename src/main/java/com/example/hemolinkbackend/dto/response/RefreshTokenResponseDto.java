package com.example.hemolinkbackend.dto.response;

public record RefreshTokenResponseDto(
    String token,
    String refreshToken,
    String tokenType,
    long expiresIn
) {}

