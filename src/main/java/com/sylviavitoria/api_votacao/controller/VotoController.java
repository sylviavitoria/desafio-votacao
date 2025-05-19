package com.sylviavitoria.api_votacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;
import com.sylviavitoria.api_votacao.interfaces.IVoto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/votos")
@RequiredArgsConstructor
@Tag(name = "Votos", description = "API para gerenciamento de votos")
public class VotoController {

    private final IVoto voto;

    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado em uma pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Associado ou pauta não encontrados"),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta pauta")
    })
    @PostMapping
    public ResponseEntity<VotoResponse> votar(@RequestBody @Valid VotoRequest request) {
        VotoResponse response = voto.registrarVoto(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar voto por ID", description = "Retorna os detalhes de um voto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado")
    })
    public ResponseEntity<VotoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(voto.buscarPorId(id));
    }

    @Operation(summary = "Consultar resultado", description = "Retorna o resultado da votação de uma pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    @GetMapping("/pautas/{pautaId}/resultado")
    public ResponseEntity<ResultadoVotacaoResponse> consultarResultado(@PathVariable Long pautaId) {
        return ResponseEntity.ok(voto.consultarResultado(pautaId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar voto", description = "Permite alterar um voto durante a sessão aberta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Sessão encerrada")
    })
    public ResponseEntity<VotoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid VotoAtualizarRequest request) {
        return ResponseEntity.ok(voto.atualizarVoto(id, request));
    }

}