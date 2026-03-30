package com.example.hemolinkbackend.mapper;

import com.example.hemolinkbackend.dto.request.HoraireDto;
import com.example.hemolinkbackend.entity.Horaire;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HoraireInitializer {

    private static final LocalTime HEURE_OUVERTURE_DEFAULT = LocalTime.of(8, 0);
    private static final LocalTime HEURE_FERMETURE_DEFAULT = LocalTime.of(17, 0);

    public List<Horaire> createDefaultHoraires() {
        List<Horaire> horaires = new ArrayList<>();
        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};

        for (int i = 0; i < jours.length; i++) {
            Horaire horaire = new Horaire();
            horaire.setJour(jours[i]);
            
            if (i < 6) {  // Lundi à Samedi
                horaire.setOuverture(HEURE_OUVERTURE_DEFAULT);
                horaire.setFermeture(HEURE_FERMETURE_DEFAULT);
                horaire.setOuvert(true);
            } else {  // Dimanche
                horaire.setOuverture(null);
                horaire.setFermeture(null);
                horaire.setOuvert(false);
            }
            
            horaires.add(horaire);
        }

        return horaires;
    }

    public List<HoraireDto> createDefaultHorairesDto() {
        List<HoraireDto> horaires = new ArrayList<>();
        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};

        for (int i = 0; i < jours.length; i++) {
            if (i < 6) {  // Lundi à Samedi
                horaires.add(new HoraireDto(
                    jours[i],
                    HEURE_OUVERTURE_DEFAULT,
                    HEURE_FERMETURE_DEFAULT,
                    true
                ));
            } else {  // Dimanche
                horaires.add(new HoraireDto(
                    jours[i],
                    null,
                    null,
                    false
                ));
            }
        }

        return horaires;
    }
}

