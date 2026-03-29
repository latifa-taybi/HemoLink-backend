package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.CentreCollecteDto;
import com.example.hemolinkbackend.dto.response.CentreCollecteResponseDto;
import com.example.hemolinkbackend.entity.CentreCollecte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = HoraireMapper.class)
public interface CentreCollecteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localisationGps", ignore = true)
    CentreCollecte toEntity(CentreCollecteDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localisationGps", ignore = true)
    @Mapping(target = "horaires", ignore = true)
    void updateEntity(CentreCollecteDto dto, @MappingTarget CentreCollecte entity);

    CentreCollecteResponseDto toResponseDto(CentreCollecte entity);
}
