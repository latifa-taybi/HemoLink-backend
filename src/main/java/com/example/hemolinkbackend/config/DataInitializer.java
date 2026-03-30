package com.example.hemolinkbackend.config;

import com.example.hemolinkbackend.entity.CentreCollecte;
import com.example.hemolinkbackend.entity.Hopital;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.repository.HopitalRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final HopitalRepository hopitalRepository;
    private final CentreCollecteRepository centreCollecteRepository;
    private final PocheSangRepository pocheSangRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        
        if (hopitalRepository.count() == 0) {
            Hopital hopital = new Hopital();
            hopital.setNom("Hôpital Moulay Youssef");
            hopital.setAdresse("Quartier des Hôpitaux");
            hopital.setVille("Casablanca");
            hopital.setTelephone("0522000000");
            hopitalRepository.save(hopital);
        }

        if (centreCollecteRepository.count() == 0) {
            CentreCollecte centre = new CentreCollecte();
            centre.setNom("CRTS Casablanca");
            centre.setAdresse("Rue Mohamed Abdou");
            centre.setVille("Casablanca");
            centre.setTelephone("0522111111");
            centre = centreCollecteRepository.save(centre);
            
            if (!utilisateurRepository.existsByEmailIgnoreCase("accueil@crts.local")) {
                Utilisateur personnelCentre = new Utilisateur();
                personnelCentre.setPrenom("Accueil");
                personnelCentre.setNom("CRTS");
                personnelCentre.setEmail("accueil@crts.local");
                personnelCentre.setTelephone("0522111112");
                personnelCentre.setRole(RoleUtilisateur.PERSONNEL_CENTRE);
                personnelCentre.setCentreCollecte(centre);
                personnelCentre.setActif(true);
                personnelCentre.setMotDePasse(passwordEncoder.encode("Crts@123"));
                personnelCentre.setCreeLe(LocalDate.now().atStartOfDay());
                utilisateurRepository.save(personnelCentre);
            }
        }

        if (pocheSangRepository.count() == 0) {
            seedPoche(GroupeSanguin.A_POS, 10);
            seedPoche(GroupeSanguin.O_NEG, 5);
            seedPoche(GroupeSanguin.B_POS, 8);
        }
    }

    private void seedPoche(GroupeSanguin groupe, int count) {
        for (int i = 0; i < count; i++) {
            PocheSang poche = new PocheSang();
            poche.setGroupeSanguin(groupe);
            poche.setStatut(StatutSang.DISPONIBLE);
            poche.setDateCollecte(LocalDate.now().minusDays(2));
            poche.setDateExpiration(LocalDate.now().plusDays(40));
            pocheSangRepository.save(poche);
        }
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
