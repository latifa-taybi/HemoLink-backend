package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.entity.Donneur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DonneurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur.id", source = "utilisateurId")
    Donneur toEntity(DonneurDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur.id", source = "utilisateurId")
    void updateEntity(DonneurDto dto, @MappingTarget Donneur entity);

    @Mapping(target = "utilisateurId", source = "utilisateur.id")
    @Mapping(target = "prenom", source = "utilisateur.prenom")
    @Mapping(target = "nom", source = "utilisateur.nom")
    @Mapping(target = "email", source = "utilisateur.email")
    @Mapping(target = "telephone", source = "utilisateur.telephone")
    @Mapping(target = "actif", source = "utilisateur.actif")
    @Mapping(target = "creeLe", source = "utilisateur.creeLe")
    DonneurResponseDto toResponseDto(Donneur entity);
}

