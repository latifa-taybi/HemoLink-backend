package com.example.hemolinkbackend.event.listener;

import com.example.hemolinkbackend.event.DonCreatedEvent;
import com.example.hemolinkbackend.event.PocheDisponibleEvent;
import com.example.hemolinkbackend.event.TestPositifEvent;
import com.example.hemolinkbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DonationWorkflowListener {

    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void onDonCreated(DonCreatedEvent event) {
        log.info("Événement DonCreatedEvent reçu");
        // TODO: Implémenter notification donneur
    }

    @EventListener
    @Transactional
    public void onTestPositif(TestPositifEvent event) {
        log.warn("Événement TestPositifEvent reçu - Test positif détecté");
        // TODO: Notifier le donneur
    }

    @EventListener
    @Transactional
    public void onPocheDisponible(PocheDisponibleEvent event) {
        log.info("Événement PocheDisponibleEvent reçu - Poche disponible");
        // TODO: Notifier le donneur et mettre à jour date dernier don
    }
}

