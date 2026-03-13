package com.example.hemolinkbackend.config;

import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@hemolink.local";
        if (utilisateurRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }

        Utilisateur admin = new Utilisateur();
        admin.setPrenom("Systeme");
        admin.setNom("Admin");
        admin.setEmail(adminEmail);
        admin.setTelephone("0000000000");
        admin.setRole(RoleUtilisateur.ADMIN);
        admin.setActif(true);
        admin.setMotDePasse(passwordEncoder.encode("Admin@123"));
        admin.setCreeLe(LocalDateTime.now());

        utilisateurRepository.save(admin);
        LOGGER.warn("Utilisateur admin initialise: {} / {}", adminEmail, "Admin@123");
    }
}

