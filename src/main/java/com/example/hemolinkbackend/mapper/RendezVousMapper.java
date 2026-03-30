package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.RendezVousDto;
import com.example.hemolinkbackend.dto.response.RendezVousResponseDto;
import com.example.hemolinkbackend.entity.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donneur.id", source = "donneurId")
    @Mapping(target = "centre.id", source = "centreId")
    RendezVous toEntity(RendezVousDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donneur.id", source = "donneurId")
    @Mapping(target = "centre.id", source = "centreId")
    void updateEntity(RendezVousDto dto, @MappingTarget RendezVous entity);

    @Mapping(target = "donneurId", source = "donneur.id")
    @Mapping(target = "donneurPrenom", source = "donneur.utilisateur.prenom")
    @Mapping(target = "donneurNom", source = "donneur.utilisateur.nom")
    @Mapping(target = "centreId", source = "centre.id")
    RendezVousResponseDto toResponseDto(RendezVous entity);
}
