package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.response.ElementCommandeResponseDto;
import com.example.hemolinkbackend.service.ElementCommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/elements-commandes")
@RequiredArgsConstructor
public class ElementCommandeController {

    private final ElementCommandeService elementCommandeService;

    @GetMapping("/{id}")
    public ElementCommandeResponseDto getById(@PathVariable Long id) {
        return elementCommandeService.getById(id);
    }

    @GetMapping
    public List<ElementCommandeResponseDto> getAll(@RequestParam(required = false) Long commandeId) {
        return commandeId != null ? elementCommandeService.getByCommandeId(commandeId) : elementCommandeService.getAll();
    }

    @PostMapping("/affecter")
    @ResponseStatus(HttpStatus.CREATED)
    public ElementCommandeResponseDto affecterPoche(@RequestParam Long commandeId, @RequestParam Long pocheSangId) {
        return elementCommandeService.affecterPoche(commandeId, pocheSangId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        elementCommandeService.supprimer(id);
    }
}

