package com.sylviavitoria.api_votacao.controller;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.interfaces.IAssociado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/associados")
@RequiredArgsConstructor
@Tag(name = "Associados", description = "API para gerenciamento de associados")
public class AssociadoController {

    private final IAssociado IAssociado;

    @Operation(summary = "Criar um novo associado", description = "Cria um novo associado com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Associado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Associado já existe com este CPF", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<AssociadoResponse> criar(@RequestBody @Valid AssociadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IAssociado.criar(request));
    }

    @Operation(summary = "Buscar associado por ID", description = "Retorna um associado com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Associado encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(IAssociado.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os associados", description = "Retorna uma lista paginada de todos os associados cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<AssociadoListarResponse>> listarTodos(
            @Parameter(description = "Número da página (começa em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação (ex: nome, cpf, email)", example = "nome") @RequestParam(required = false) String sort) {

        return ResponseEntity.ok(IAssociado.listarTodos(page, size, sort));
    }

    @Operation(summary = "Atualizar associado", description = "Atualiza os dados de um associado existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Associado atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Associado já existe com este CPF", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AssociadoResponse> atualizar(@PathVariable Long id,
            @RequestBody @Valid AssociadoRequest request) {
        return ResponseEntity.ok(IAssociado.atualizar(id, request));
    }

    @Operation(summary = "Excluir associado", description = "Remove um associado com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Associado removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        IAssociado.deletar(id);
        return ResponseEntity.noContent().build();
    }

}