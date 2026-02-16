package com.example.hemolinkbackend.dto.response;

import java.time.LocalDateTime;

public record TestLaboResponseDto(
        Long id,
        Long pocheSangId,
        Boolean vih,
        Boolean hepatiteB,
        Boolean hepatiteC,
        Boolean syphilis,
        LocalDateTime dateTest,
        Long technicienLaboId
) {
}

