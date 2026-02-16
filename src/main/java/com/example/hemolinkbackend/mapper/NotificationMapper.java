package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.NotificationDto;
import com.example.hemolinkbackend.dto.response.NotificationResponseDto;
import com.example.hemolinkbackend.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur.id", source = "utilisateurId")
    Notification toEntity(NotificationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur.id", source = "utilisateurId")
    void updateEntity(NotificationDto dto, @MappingTarget Notification entity);

    @Mapping(target = "utilisateurId", source = "utilisateur.id")
    NotificationResponseDto toResponseDto(Notification entity);
}
