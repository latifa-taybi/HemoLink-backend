package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.NotificationDto;
import com.example.hemolinkbackend.dto.response.NotificationResponseDto;
import com.example.hemolinkbackend.entity.Notification;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.mapper.NotificationMapper;
import com.example.hemolinkbackend.repository.NotificationRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.NotificationService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponseDto creer(NotificationDto dto) {
        Utilisateur utilisateur = getUtilisateurById(dto.utilisateurId());
        Notification notification = notificationMapper.toEntity(dto);
        notification.setUtilisateur(utilisateur);
        notification.setLu(dto.lu() != null && dto.lu());
        notification.setCreeLe(dto.creeLe() != null ? dto.creeLe() : LocalDateTime.now());
        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponseDto notifierUtilisateur(Long utilisateurId, String message) {
        return creer(new NotificationDto(utilisateurId, message, false, LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto getById(Long id) {
        return notificationMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getByUtilisateurId(Long utilisateurId) {
        return notificationRepository.findByUtilisateurIdOrderByCreeLeDesc(utilisateurId)
                .stream()
                .map(notificationMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNonLuesByUtilisateurId(Long utilisateurId) {
        return notificationRepository.findByUtilisateurIdAndLuFalseOrderByCreeLeDesc(utilisateurId)
                .stream()
                .map(notificationMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long compterNonLues(Long utilisateurId) {
        return notificationRepository.countByUtilisateurIdAndLuFalse(utilisateurId);
    }

    @Override
    public NotificationResponseDto marquerCommeLue(Long id) {
        Notification notification = getEntityById(id);
        notification.setLu(true);
        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    private Notification getEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Notification introuvable : " + id));
    }

    private Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + id));
    }
}
