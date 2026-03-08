package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.TestLaboDto;
import com.example.hemolinkbackend.dto.response.TestLaboResponseDto;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.entity.TestLabo;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.TestLaboMapper;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.repository.TestLaboRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.NotificationService;
import com.example.hemolinkbackend.service.TestLaboService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TestLaboServiceImpl implements TestLaboService {

    private final TestLaboRepository testLaboRepository;
    private final PocheSangRepository pocheSangRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final TestLaboMapper testLaboMapper;

    @Override
    public TestLaboResponseDto enregistrerResultat(TestLaboDto dto) {
        PocheSang pocheSang = getPocheSangById(dto.pocheSangId());
        if (testLaboRepository.existsByPocheSangId(dto.pocheSangId())) {
            throw new RegleMetierException("Un test existe deja pour cette poche de sang.");
        }

        TestLabo testLabo = testLaboMapper.toEntity(dto);
        testLabo.setPocheSang(pocheSang);
        testLabo.setDateTest(dto.dateTest() != null ? dto.dateTest() : LocalDateTime.now());
        if (dto.technicienLaboId() != null) {
            testLabo.setTechnicienLabo(getTechnicienById(dto.technicienLaboId()));
        }

        TestLabo saved = testLaboRepository.save(testLabo);

        if (estPositif(saved)) {
            pocheSang.setStatut(StatutSang.ECARTEE);
            notifierDonneurSiPossible(pocheSang, "Votre don a ete place au rebut suite aux tests biologiques.");
        } else {
            pocheSang.setStatut(StatutSang.DISPONIBLE);
        }
        pocheSangRepository.save(pocheSang);

        return testLaboMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TestLaboResponseDto getById(Long id) {
        return testLaboMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestLaboResponseDto> getAll() {
        return testLaboRepository.findAll().stream().map(testLaboMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TestLaboResponseDto getByPocheSangId(Long pocheSangId) {
        return testLaboMapper.toResponseDto(testLaboRepository.findByPocheSangId(pocheSangId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Test introuvable pour la poche : " + pocheSangId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestLaboResponseDto> getByTechnicienEtPeriode(Long technicienLaboId, LocalDateTime debut, LocalDateTime fin) {
        return testLaboRepository.findByTechnicienLaboIdAndDateTestBetween(technicienLaboId, debut, fin)
                .stream()
                .map(testLaboMapper::toResponseDto)
                .toList();
    }

    private boolean estPositif(TestLabo testLabo) {
        return Boolean.TRUE.equals(testLabo.getVih())
                || Boolean.TRUE.equals(testLabo.getHepatiteB())
                || Boolean.TRUE.equals(testLabo.getHepatiteC())
                || Boolean.TRUE.equals(testLabo.getSyphilis());
    }

    private void notifierDonneurSiPossible(PocheSang pocheSang, String message) {
        if (pocheSang.getDon() != null
                && pocheSang.getDon().getDonneur() != null
                && pocheSang.getDon().getDonneur().getUtilisateur() != null) {
            notificationService.notifierUtilisateur(pocheSang.getDon().getDonneur().getUtilisateur().getId(), message);
        }
    }

    private TestLabo getEntityById(Long id) {
        return testLaboRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Test laboratoire introuvable : " + id));
    }

    private PocheSang getPocheSangById(Long id) {
        return pocheSangRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Poche de sang introuvable : " + id));
    }

    private Utilisateur getTechnicienById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Technicien introuvable : " + id));
    }
}
