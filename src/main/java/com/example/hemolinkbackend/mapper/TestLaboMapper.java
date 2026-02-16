package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.TestLaboDto;
import com.example.hemolinkbackend.dto.response.TestLaboResponseDto;
import com.example.hemolinkbackend.entity.TestLabo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TestLaboMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pocheSang.id", source = "pocheSangId")
    @Mapping(target = "technicienLabo.id", source = "technicienLaboId")
    TestLabo toEntity(TestLaboDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pocheSang.id", source = "pocheSangId")
    @Mapping(target = "technicienLabo.id", source = "technicienLaboId")
    void updateEntity(TestLaboDto dto, @MappingTarget TestLabo entity);

    @Mapping(target = "pocheSangId", source = "pocheSang.id")
    @Mapping(target = "technicienLaboId", source = "technicienLabo.id")
    TestLaboResponseDto toResponseDto(TestLabo entity);
}
