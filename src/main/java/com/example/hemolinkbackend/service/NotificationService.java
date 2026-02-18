package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.NotificationDto;
import com.example.hemolinkbackend.dto.response.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto creer(NotificationDto dto);

    NotificationResponseDto notifierUtilisateur(Long utilisateurId, String message);

    NotificationResponseDto getById(Long id);

    List<NotificationResponseDto> getByUtilisateurId(Long utilisateurId);

    List<NotificationResponseDto> getNonLuesByUtilisateurId(Long utilisateurId);

    long compterNonLues(Long utilisateurId);

    NotificationResponseDto marquerCommeLue(Long id);
}
