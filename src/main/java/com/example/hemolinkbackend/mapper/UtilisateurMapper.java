package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.UtilisateurDto;
import com.example.hemolinkbackend.dto.response.UtilisateurResponseDto;
import com.example.hemolinkbackend.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "actif", expression = "java(dto.actif() != null && dto.actif())")
    Utilisateur toEntity(UtilisateurDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "actif", expression = "java(dto.actif() != null && dto.actif())")
    void updateEntity(UtilisateurDto dto, @MappingTarget Utilisateur entity);

    UtilisateurResponseDto toResponseDto(Utilisateur entity);
}
