package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.StatistiquesStockDto;
import com.example.hemolinkbackend.dto.response.StatistiquesStockResponseDto;
import com.example.hemolinkbackend.entity.StatistiquesStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StatistiquesStockMapper {

    @Mapping(target = "id", ignore = true)
    StatistiquesStock toEntity(StatistiquesStockDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(StatistiquesStockDto dto, @MappingTarget StatistiquesStock entity);

    StatistiquesStockResponseDto toResponseDto(StatistiquesStock entity);
}
