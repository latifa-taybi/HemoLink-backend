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
    DonneurResponseDto toResponseDto(Donneur entity);
}
