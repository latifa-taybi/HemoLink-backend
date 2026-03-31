package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.mapper.DonneurMapper;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.DonService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneurServiceImplTest {

    @Mock
    private DonneurRepository donneurRepository;
    @Mock
    private DonneurMapper donneurMapper;
    @Mock
    private DonService donService;
    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private DonneurServiceImpl donneurService;

    private Donneur donneur;
    private DonneurDto donneurDto;
    private DonneurResponseDto donneurResponseDto;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPrenom("John");
        utilisateur.setNom("Doe");
        utilisateur.setEmail("john@example.com");
        utilisateur.setTelephone("0123456789");
        utilisateur.setRole(RoleUtilisateur.DONNEUR);
        utilisateur.setActif(true);
        utilisateur.setCreeLe(LocalDateTime.now());

        donneur = new Donneur();
        donneur.setId(1L);
        donneur.setUtilisateur(utilisateur);
        donneur.setGroupeSanguin(GroupeSanguin.A_POS);
        donneur.setDateNaissance(LocalDate.of(1990, 1, 1));
        donneur.setPoids(75.5);
        donneur.setNombreDonsAnnuel(0);

        donneurDto = new DonneurDto(1L, GroupeSanguin.A_POS, LocalDate.of(1990, 1, 1), 75.5, null, 0);
        
        donneurResponseDto = new DonneurResponseDto(
                1L, 1L, "John", "Doe", "john@example.com", "0123456789", 
                GroupeSanguin.A_POS, LocalDate.of(1990, 1, 1), 75.5, null, 0, true, LocalDateTime.now());
    }

    @Test
    void creer_ShouldReturnDonneurResponseDto() {
        when(donneurMapper.toEntity(any(DonneurDto.class))).thenReturn(donneur);
        when(donneurRepository.save(any(Donneur.class))).thenReturn(donneur);
        when(donneurMapper.toResponseDto(any(Donneur.class))).thenReturn(donneurResponseDto);

        DonneurResponseDto result = donneurService.creer(donneurDto);

        assertNotNull(result);
        assertEquals(donneurResponseDto.id(), result.id());
        verify(donneurRepository).save(any(Donneur.class));
    }

    @Test
    void getById_WhenExists_ShouldReturnDonneurResponseDto() {
        when(donneurRepository.findById(1L)).thenReturn(Optional.of(donneur));
        when(donneurMapper.toResponseDto(donneur)).thenReturn(donneurResponseDto);

        DonneurResponseDto result = donneurService.getById(1L);

        assertNotNull(result);
        assertEquals(donneurResponseDto.id(), result.id());
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(donneurRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class, () -> donneurService.getById(1L));
    }

    @Test
    void getAll_ShouldReturnList() {
        when(donneurRepository.findAll()).thenReturn(List.of(donneur));
        when(donneurMapper.toResponseDto(donneur)).thenReturn(donneurResponseDto);

        List<DonneurResponseDto> result = donneurService.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void mettreAJour_WhenExists_ShouldReturnUpdatedDto() {
        when(donneurRepository.findById(1L)).thenReturn(Optional.of(donneur));
        doNothing().when(donneurMapper).updateEntity(any(DonneurDto.class), any(Donneur.class));
        when(donneurRepository.save(donneur)).thenReturn(donneur);
        when(donneurMapper.toResponseDto(donneur)).thenReturn(donneurResponseDto);

        DonneurResponseDto result = donneurService.mettreAJour(1L, donneurDto);

        assertNotNull(result);
        verify(donneurRepository).save(donneur);
    }

    @Test
    void supprimer_WhenExists_ShouldCallDelete() {
        when(donneurRepository.findById(1L)).thenReturn(Optional.of(donneur));
        doNothing().when(donneurRepository).delete(donneur);

        donneurService.supprimer(1L);

        verify(donneurRepository).delete(donneur);
    }

    @Test
    void getByUtilisateurId_WhenDonneurExists_ShouldReturnDto() {
        when(donneurRepository.findByUtilisateurId(1L)).thenReturn(Optional.of(donneur));
        when(donneurMapper.toResponseDto(donneur)).thenReturn(donneurResponseDto);

        DonneurResponseDto result = donneurService.getByUtilisateurId(1L);

        assertNotNull(result);
        verify(donneurRepository, never()).save(any());
    }

    @Test
    void getByUtilisateurId_WhenDonneurNotExistsAndUserIsDonneur_ShouldCreateAndReturnDto() {
        when(donneurRepository.findByUtilisateurId(1L)).thenReturn(Optional.empty());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(donneurRepository.save(any(Donneur.class))).thenReturn(donneur);
        when(donneurMapper.toResponseDto(any(Donneur.class))).thenReturn(donneurResponseDto);

        DonneurResponseDto result = donneurService.getByUtilisateurId(1L);

        assertNotNull(result);
        verify(donneurRepository).save(any(Donneur.class));
    }

    @Test
    void getByUtilisateurId_WhenUserNotExists_ShouldThrowException() {
        when(donneurRepository.findByUtilisateurId(1L)).thenReturn(Optional.empty());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class, () -> donneurService.getByUtilisateurId(1L));
    }

    @Test
    void getByUtilisateurId_WhenUserIsNotDonneur_ShouldThrowException() {
        utilisateur.setRole(RoleUtilisateur.ADMIN);
        when(donneurRepository.findByUtilisateurId(1L)).thenReturn(Optional.empty());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        assertThrows(RessourceNonTrouveeException.class, () -> donneurService.getByUtilisateurId(1L));
    }

    @Test
    void verifierEligibilite_ShouldDelegateToDonService() {
        when(donService.verifierEligibilite(1L)).thenReturn(true);
        
        boolean result = donneurService.verifierEligibilite(1L);
        
        assertTrue(result);
        verify(donService).verifierEligibilite(1L);
    }

    @Test
    void calculerProchaineDateEligible_ShouldDelegateToDonService() {
        LocalDate expectedDate = LocalDate.now().plusWeeks(8);
        when(donService.calculerProchaineDateEligible(1L)).thenReturn(expectedDate);
        
        LocalDate result = donneurService.calculerProchaineDateEligible(1L);
        
        assertEquals(expectedDate, result);
        verify(donService).calculerProchaineDateEligible(1L);
    }
}
