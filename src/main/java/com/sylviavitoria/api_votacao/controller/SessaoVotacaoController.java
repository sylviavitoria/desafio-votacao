package com.sylviavitoria.api_votacao.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;
import com.sylviavitoria.api_votacao.interfaces.ISessaoVotacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação", description = "API para gerenciamento de sessões de votação")
public class SessaoVotacaoController {

        private final ISessaoVotacao sessaoVotacao;

        @Operation(summary = "Criar uma nova sessão de votação", description = "Cria uma nova sessão de votação para uma pauta. É possível abrir imediatamente informando duracaoMinutos, "
                        + "ou agendar para datas específicas informando dataInicio e dataFim.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
                                        @ExampleObject(name = "Abertura Imediata", value = """
                                                        {
                                                          "pautaId": 1,
                                                          "duracaoMinutos": 5
                                                        }
                                                        """),
                                        @ExampleObject(name = "Agendamento", value = """
                                                        {
                                                          "pautaId": 1,
                                                          "dataInicio": "2025-05-25T22:00:00",
                                                          "dataFim": "2025-05-26T11:00:00"
                                                        }
                                                        """)
                        })))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Sessão de votação criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = Map.class))),
                        @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = Map.class))),
                        @ApiResponse(responseCode = "409", description = "Erro de negócio (ex: pauta já possui sessão)", content = @Content(schema = @Schema(implementation = Map.class)))
        })
        @PostMapping
        public ResponseEntity<SessaoVotacaoResponse> criar(@RequestBody @Valid SessaoVotacaoRequest request) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(sessaoVotacao.criar(request));
        }

        @Operation(summary = "Consultar status da sessão de votação", description = "Retorna os detalhes e status atual de uma sessão de votação")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
        })
        @GetMapping("/{id}")
        public ResponseEntity<SessaoVotacaoResponse> consultar(@PathVariable Long id) {
                return ResponseEntity.ok(sessaoVotacao.buscarPorId(id));
        }

        @GetMapping
        @Operation(summary = "Listar todas as sessões", description = "Retorna uma lista paginada de todas as sessões cadastradas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        public ResponseEntity<Page<SessaoVotacaoResponse>> listarTodos(
                        @Parameter(description = "Número da página (começa em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Campo para ordenação (ex: dataAbertura, status)", example = "dataAbertura") @RequestParam(required = false) String sort) {
                return ResponseEntity.ok(sessaoVotacao.listarTodos(page, size, sort));
        }

        @Operation(summary = "Atualizar período da sessão", description = "Permite estender o período de votação", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
                        @ExampleObject(name = "Adicionar Minutos", value = """
                                        {
                                          "minutosAdicionais": 30
                                        }
                                        """),
                        @ExampleObject(name = "Nova Data Fim", value = """
                                        {
                                          "dataFim": "2025-05-27T00:00:00"
                                        }
                                        """)
        })))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Período atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
                        @ApiResponse(responseCode = "409", description = "Sessão já finalizada")
        })
        @PutMapping("/{id}/periodo")
        public ResponseEntity<SessaoVotacaoResponse> atualizarPeriodo(
                        @PathVariable Long id,
                        @RequestBody @Valid SessaoVotacaoAtualizarRequest request) {
                return ResponseEntity.ok(sessaoVotacao.atualizarPeriodo(id, request));
        }
}