package com.example.hemolinkbackend.dto.response;

import java.time.LocalDateTime;

public record DonResponseDto(
        Long id,
        Long donneurId,
        Long centreId,
        LocalDateTime dateDon,
        Integer volumeMl
) {
}

