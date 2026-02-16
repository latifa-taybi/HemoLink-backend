package com.example.hemolinkbackend.dto.request;

import java.time.LocalDateTime;

public record NotificationDto(
        Long utilisateurId,
        String message,
        Boolean lu,
        LocalDateTime creeLe
) {
}

