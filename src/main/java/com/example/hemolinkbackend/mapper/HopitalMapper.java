package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.HopitalDto;
import com.example.hemolinkbackend.dto.response.HopitalResponseDto;
import com.example.hemolinkbackend.entity.Hopital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HopitalMapper {

    @Mapping(target = "id", ignore = true)
    Hopital toEntity(HopitalDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(HopitalDto dto, @MappingTarget Hopital entity);

    HopitalResponseDto toResponseDto(Hopital entity);
}
