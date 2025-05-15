package com.sylviavitoria.api_votacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.interfaces.IPauta;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "API para gerenciamento de pautas")
public class PautaController {

    private final IPauta iPauta;

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@RequestBody @Valid PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iPauta.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(iPauta.buscarPorId(id));
    }

}