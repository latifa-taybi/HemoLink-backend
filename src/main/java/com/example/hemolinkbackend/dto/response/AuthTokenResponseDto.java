package com.example.hemolinkbackend.dto.response;

public record AuthTokenResponseDto(
        String token,
        String type,
        Long expiresIn
) {
}

