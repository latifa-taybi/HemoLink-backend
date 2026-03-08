package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.TestLaboDto;
import com.example.hemolinkbackend.dto.response.TestLaboResponseDto;
import com.example.hemolinkbackend.service.TestLaboService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tests-labo")
@RequiredArgsConstructor
public class TestLaboController {

    private final TestLaboService testLaboService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestLaboResponseDto enregistrer(@RequestBody TestLaboDto dto) {
        return testLaboService.enregistrerResultat(dto);
    }

    @GetMapping("/{id}")
    public TestLaboResponseDto getById(@PathVariable Long id) {
        return testLaboService.getById(id);
    }

    @GetMapping
    public List<TestLaboResponseDto> getAll(@RequestParam(required = false) Long technicienLaboId,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        if (technicienLaboId != null && debut != null && fin != null) {
            return testLaboService.getByTechnicienEtPeriode(technicienLaboId, debut, fin);
        }
        return testLaboService.getAll();
    }

    @GetMapping("/poche/{pocheSangId}")
    public TestLaboResponseDto getByPoche(@PathVariable Long pocheSangId) {
        return testLaboService.getByPocheSangId(pocheSangId);
    }
}

