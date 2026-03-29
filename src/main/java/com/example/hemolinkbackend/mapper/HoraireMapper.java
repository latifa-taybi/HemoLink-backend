package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.HoraireDto;
import com.example.hemolinkbackend.dto.response.HoraireResponseDto;
import com.example.hemolinkbackend.entity.Horaire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HoraireMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "centreCollecte", ignore = true)
    Horaire toEntity(HoraireDto dto);

    HoraireResponseDto toResponseDto(Horaire entity);
}
