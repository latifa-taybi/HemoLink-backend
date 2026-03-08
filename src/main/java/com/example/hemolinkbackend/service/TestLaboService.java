package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.TestLaboDto;
import com.example.hemolinkbackend.dto.response.TestLaboResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TestLaboService {

    TestLaboResponseDto enregistrerResultat(TestLaboDto dto);

    TestLaboResponseDto getById(Long id);

    List<TestLaboResponseDto> getAll();

    TestLaboResponseDto getByPocheSangId(Long pocheSangId);

    List<TestLaboResponseDto> getByTechnicienEtPeriode(Long technicienLaboId, LocalDateTime debut, LocalDateTime fin);
}
