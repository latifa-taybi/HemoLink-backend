package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.PocheSangDto;
import com.example.hemolinkbackend.dto.response.PocheSangResponseDto;
import com.example.hemolinkbackend.entity.PocheSang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PocheSangMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "don.id", source = "donId")
    PocheSang toEntity(PocheSangDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "don.id", source = "donId")
    void updateEntity(PocheSangDto dto, @MappingTarget PocheSang entity);

    @Mapping(target = "donId", source = "don.id")
    PocheSangResponseDto toResponseDto(PocheSang entity);
}
