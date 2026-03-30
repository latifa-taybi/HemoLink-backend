    package com.example.hemolinkbackend.service.impl;

    import com.example.hemolinkbackend.dto.request.CentreCollecteDto;
    import com.example.hemolinkbackend.dto.response.CentreCollecteResponseDto;
    import com.example.hemolinkbackend.entity.CentreCollecte;
    import com.example.hemolinkbackend.mapper.CentreCollecteMapper;
    import com.example.hemolinkbackend.repository.CentreCollecteRepository;
    import com.example.hemolinkbackend.service.CentreCollecteService;
    import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    @Transactional
    @RequiredArgsConstructor
    @Slf4j
    public class CentreCollecteServiceImpl implements CentreCollecteService {

        private final CentreCollecteRepository centreCollecteRepository;
        private final CentreCollecteMapper centreCollecteMapper;

        @Override
        public CentreCollecteResponseDto creer(CentreCollecteDto dto) {
            log.info("Création d'un centre de collecte: {}", dto.nom());
            CentreCollecte centre = centreCollecteMapper.toEntity(dto);
            centre.initializeLocalization();

            if (centre.getHoraires() == null || centre.getHoraires().isEmpty()) {
                for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
                    com.example.hemolinkbackend.entity.Horaire horaire = new com.example.hemolinkbackend.entity.Horaire();
                    horaire.setJour(day.name());
                    if (day == java.time.DayOfWeek.SUNDAY) {
                        horaire.setOuvert(false);
                        horaire.setOuverture(null);
                        horaire.setFermeture(null);
                    } else {
                        horaire.setOuvert(true);
                        horaire.setOuverture(java.time.LocalTime.of(8, 0));
                        horaire.setFermeture(java.time.LocalTime.of(17, 0));
                    }
                    centre.addHoraire(horaire);
                }
            } else {
                centre.getHoraires().forEach(h -> h.setCentreCollecte(centre));
            }

            CentreCollecte saved = centreCollecteRepository.save(centre);
            return centreCollecteMapper.toResponseDto(saved);
        }

        @Override
        @Transactional(readOnly = true)
        public CentreCollecteResponseDto getById(Long id) {
            log.debug("Récupération du centre ID: {}", id);
            return centreCollecteMapper.toResponseDto(getEntityById(id));
        }

        @Override
        @Transactional(readOnly = true)
        public List<CentreCollecteResponseDto> getAll() {
            log.debug("Récupération de tous les centres");
            return centreCollecteRepository.findAll()
                    .stream()
                    .map(centreCollecteMapper::toResponseDto)
                    .toList();
        }

        @Override
        public CentreCollecteResponseDto mettreAJour(Long id, CentreCollecteDto dto) {
            log.info("Mise à jour du centre ID: {}", id);
            CentreCollecte centre = getEntityById(id);
            centreCollecteMapper.updateEntity(dto, centre);
            centre.initializeLocalization();

            if (dto.horaires() != null) {
                centre.getHoraires().clear();
                centre.getHoraires().addAll(dto.horaires().stream()
                        .map(hDto -> {
                            var h = new com.example.hemolinkbackend.entity.Horaire();
                            h.setJour(hDto.jour());
                            h.setOuverture(hDto.ouverture());
                            h.setFermeture(hDto.fermeture());
                            h.setOuvert(hDto.ouvert());
                            h.setCentreCollecte(centre);
                            return h;
                        }).toList());
            }

            CentreCollecte updated = centreCollecteRepository.save(centre);
            return centreCollecteMapper.toResponseDto(updated);
        }

        @Override
        public void supprimer(Long id) {
            log.warn("Suppression du centre ID: {}", id);
            centreCollecteRepository.delete(getEntityById(id));
        }

        @Override
        @Transactional(readOnly = true)
        public List<CentreCollecteResponseDto> rechercherPlusProches(Double latitude, Double longitude) {
            log.debug("Recherche des centres proches de lat: {}, long: {}", latitude, longitude);
            return centreCollecteRepository.findAll()
                    .stream()
                    .map(centreCollecteMapper::toResponseDto)
                    .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<CentreCollecteResponseDto> rechercherParNom(String nom) {
            log.debug("Recherche de centres par nom: {}", nom);
            return centreCollecteRepository.findAll()
                    .stream()
                    .map(centreCollecteMapper::toResponseDto)
                    .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<CentreCollecteResponseDto> rechercherParVille(String ville) {
            log.debug("Recherche de centres par ville: {}", ville);
            return centreCollecteRepository.findAll()
                    .stream()
                    .map(centreCollecteMapper::toResponseDto)
                    .toList();
        }

        private CentreCollecte getEntityById(Long id) {
            return centreCollecteRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Centre non trouvé avec ID: {}", id);
                        return new RessourceNonTrouveeException("Centre de collecte introuvable : " + id);
                    });
        }
    }

