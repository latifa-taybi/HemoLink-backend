package com.example.hemolinkbackend.config;

import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        String adminEmail = "admin@hemolink.com";
        
        if (utilisateurRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
            return;
        }

        Utilisateur admin = new Utilisateur();
        admin.setPrenom("Administrateur");
        admin.setNom("HémoLink");
        admin.setEmail(adminEmail);
        admin.setTelephone("0600000000");
        admin.setMotDePasse(passwordEncoder.encode("admin123"));
        admin.setRole(RoleUtilisateur.ADMIN);
        admin.setActif(true);
        admin.setCreeLe(LocalDate.now().atStartOfDay());
        
        utilisateurRepository.save(admin);
    }
}


