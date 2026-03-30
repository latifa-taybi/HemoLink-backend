package com.example.hemolinkbackend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.hemolinkbackend.dto.request.HoraireDto;
import com.example.hemolinkbackend.entity.Horaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour convertir les horaires entre JSON String et List<Horaire>
 */
@Slf4j
@Component
public class HoraireJsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    public List<Horaire> jsonToHoraires(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Horaire.class));
        } catch (Exception e) {
            log.error("Erreur lors de la conversion JSON vers Horaires: {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    public String horairesToJson(List<Horaire> horaires) {
        if (horaires == null || horaires.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(horaires);
        } catch (Exception e) {
            log.error("Erreur lors de la conversion Horaires vers JSON: {}", e.getMessage());
            return "[]";
        }
    }


    public String horaireDtosToJson(List<HoraireDto> horaires) {
        if (horaires == null || horaires.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(horaires);
        } catch (Exception e) {
            log.error("Erreur lors de la conversion HoraireDto vers JSON: {}", e.getMessage());
            return "[]";
        }
    }


    public List<HoraireDto> jsonToHoraireDtos(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, HoraireDto.class));
        } catch (Exception e) {
            log.error("Erreur lors de la conversion JSON vers HoraireDto: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}

