package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.ElementCommandeDto;
import com.example.hemolinkbackend.dto.response.ElementCommandeResponseDto;
import com.example.hemolinkbackend.entity.ElementCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ElementCommandeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande.id", source = "commandeId")
    @Mapping(target = "pocheSang.id", source = "pocheSangId")
    ElementCommande toEntity(ElementCommandeDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande.id", source = "commandeId")
    @Mapping(target = "pocheSang.id", source = "pocheSangId")
    void updateEntity(ElementCommandeDto dto, @MappingTarget ElementCommande entity);

    @Mapping(target = "commandeId", source = "commande.id")
    @Mapping(target = "pocheSangId", source = "pocheSang.id")
    ElementCommandeResponseDto toResponseDto(ElementCommande entity);
}
