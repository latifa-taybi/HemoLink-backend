package com.example.hemolinkbackend.dto.request;

import java.time.LocalDateTime;

public record DonDto(
        Long donneurId,
        Long centreId,
        LocalDateTime dateDon,
        Integer volumeMl
) {
}

