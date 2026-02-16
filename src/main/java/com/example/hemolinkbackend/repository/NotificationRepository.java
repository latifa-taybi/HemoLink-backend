package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurIdOrderByCreeLeDesc(Long utilisateurId);

    List<Notification> findByUtilisateurIdAndLuFalseOrderByCreeLeDesc(Long utilisateurId);

    long countByUtilisateurIdAndLuFalse(Long utilisateurId);
}

