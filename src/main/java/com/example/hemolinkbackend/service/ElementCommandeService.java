package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.response.ElementCommandeResponseDto;

import java.util.List;

public interface ElementCommandeService {

    ElementCommandeResponseDto getById(Long id);

    List<ElementCommandeResponseDto> getAll();

    List<ElementCommandeResponseDto> getByCommandeId(Long commandeId);

    ElementCommandeResponseDto affecterPoche(Long commandeId, Long pocheSangId);

    void supprimer(Long id);
}
