package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;
import com.example.hemolinkbackend.service.CommandeSangService;
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

@RestController
@RequestMapping("/api/commandes-sang")
@RequiredArgsConstructor
public class CommandeSangController {

    private final CommandeSangService commandeSangService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeSangResponseDto creer(@RequestBody CommandeSangDto dto) {
        return commandeSangService.creer(dto);
    }

    @GetMapping("/{id}")
    public CommandeSangResponseDto getById(@PathVariable Long id) {
        return commandeSangService.getById(id);
    }

    @GetMapping
    public List<CommandeSangResponseDto> getAll(@RequestParam(required = false) Long hopitalId) {
        return hopitalId != null ? commandeSangService.getByHopital(hopitalId) : commandeSangService.getAll();
    }

    @GetMapping("/urgences")
    public List<CommandeSangResponseDto> getUrgencesActives() {
        return commandeSangService.getUrgencesActives();
    }

    @PatchMapping("/{id}/preparer")
    public CommandeSangResponseDto preparer(@PathVariable Long id) {
        return commandeSangService.preparerCommande(id);
    }

    @PatchMapping("/{id}/expedier")
    public CommandeSangResponseDto expedier(@PathVariable Long id) {
        return commandeSangService.expedierCommande(id);
    }

    @PatchMapping("/{id}/livrer")
    public CommandeSangResponseDto livrer(@PathVariable Long id) {
        return commandeSangService.livrerCommande(id);
    }

    @PatchMapping("/{id}/annuler")
    public CommandeSangResponseDto annuler(@PathVariable Long id) {
        return commandeSangService.annulerCommande(id);
    }
}

