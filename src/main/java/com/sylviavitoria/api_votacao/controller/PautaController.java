package com.sylviavitoria.api_votacao.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sylviavitoria.api_votacao.dto.PautaAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.interfaces.IPauta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "API para gerenciamento de pautas de votação")
public class PautaController {

    private final IPauta iPauta;

    @Operation(summary = "Criar uma nova pauta", description = "Cria uma nova pauta com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Criador não encontrado", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<PautaResponse> criar(@RequestBody @Valid PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iPauta.criar(request));
    }

    @Operation(summary = "Buscar pauta por ID", description = "Retorna uma pauta com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(iPauta.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pautas", description = "Retorna uma lista paginada de todas as pautas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<PautaResponse>> listarTodos(
            @Parameter(description = "Número da página (começa em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação (ex: titulo, dataCriacao, status)", example = "titulo") @RequestParam(required = false) String sort) {

        return ResponseEntity.ok(iPauta.listarTodos(page, size, sort));
    }

    @Operation(summary = "Atualizar pauta", description = "Atualiza os dados de uma pauta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Não é possível editar uma pauta que já está em votação ou encerrada", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    ResponseEntity<PautaResponse> atualizar(@PathVariable Long id,
            @RequestBody @Valid PautaAtualizarRequest request) {
        return ResponseEntity.ok(iPauta.atualizar(id, request));
    }

    @Operation(summary = "Excluir pauta", description = "Remove uma pauta com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pauta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Não é possível deletar uma pauta que está em votação", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        iPauta.deletar(id);
        return ResponseEntity.noContent().build();
    }

}