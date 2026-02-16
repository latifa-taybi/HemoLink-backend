package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;
import com.example.hemolinkbackend.entity.Don;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donneur.id", source = "donneurId")
    @Mapping(target = "centre.id", source = "centreId")
    Don toEntity(DonDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donneur.id", source = "donneurId")
    @Mapping(target = "centre.id", source = "centreId")
    void updateEntity(DonDto dto, @MappingTarget Don entity);

    @Mapping(target = "donneurId", source = "donneur.id")
    @Mapping(target = "centreId", source = "centre.id")
    DonResponseDto toResponseDto(Don entity);
}
