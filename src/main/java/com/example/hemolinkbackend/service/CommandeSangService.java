package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;

import java.util.List;

public interface CommandeSangService {

    CommandeSangResponseDto creer(CommandeSangDto dto);

    CommandeSangResponseDto getById(Long id);

    List<CommandeSangResponseDto> getAll();

    List<CommandeSangResponseDto> getByHopital(Long hopitalId);

    List<CommandeSangResponseDto> getUrgencesActives();

    CommandeSangResponseDto preparerCommande(Long id);

    CommandeSangResponseDto expedierCommande(Long id);

    CommandeSangResponseDto livrerCommande(Long id);

    CommandeSangResponseDto annulerCommande(Long id);
}
