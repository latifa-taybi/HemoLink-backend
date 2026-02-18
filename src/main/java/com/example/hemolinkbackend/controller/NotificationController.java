package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.NotificationDto;
import com.example.hemolinkbackend.dto.response.NotificationResponseDto;
import com.example.hemolinkbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDto creer(@RequestBody NotificationDto dto) {
        return notificationService.creer(dto);
    }

    @PostMapping("/utilisateur/{utilisateurId}")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDto notifierUtilisateur(@PathVariable Long utilisateurId, @RequestParam String message) {
        return notificationService.notifierUtilisateur(utilisateurId, message);
    }

    @GetMapping("/{id}")
    public NotificationResponseDto getById(@PathVariable Long id) {
        return notificationService.getById(id);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<NotificationResponseDto> getByUtilisateur(@PathVariable Long utilisateurId) {
        return notificationService.getByUtilisateurId(utilisateurId);
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public List<NotificationResponseDto> getNonLues(@PathVariable Long utilisateurId) {
        return notificationService.getNonLuesByUtilisateurId(utilisateurId);
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues/count")
    public Map<String, Object> compterNonLues(@PathVariable Long utilisateurId) {
        return Map.of("utilisateurId", utilisateurId, "count", notificationService.compterNonLues(utilisateurId));
    }

    @PatchMapping("/{id}/lire")
    public NotificationResponseDto marquerCommeLue(@PathVariable Long id) {
        return notificationService.marquerCommeLue(id);
    }
}

