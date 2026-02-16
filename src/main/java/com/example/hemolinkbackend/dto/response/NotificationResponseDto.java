package com.example.hemolinkbackend.dto.response;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        Long id,
        Long utilisateurId,
        String message,
        Boolean lu,
        LocalDateTime creeLe
) {
}

