package com.example.hemolinkbackend.dto.request;

import java.time.LocalDateTime;

public record TestLaboDto(
        Long pocheSangId,
        Boolean vih,
        Boolean hepatiteB,
        Boolean hepatiteC,
        Boolean syphilis,
        LocalDateTime dateTest,
        Long technicienLaboId
) {
}

