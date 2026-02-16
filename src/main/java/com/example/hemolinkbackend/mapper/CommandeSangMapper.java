package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;
import com.example.hemolinkbackend.entity.CommandeSang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommandeSangMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hopital.id", source = "hopitalId")
    CommandeSang toEntity(CommandeSangDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hopital.id", source = "hopitalId")
    void updateEntity(CommandeSangDto dto, @MappingTarget CommandeSang entity);

    @Mapping(target = "hopitalId", source = "hopital.id")
    CommandeSangResponseDto toResponseDto(CommandeSang entity);
}
