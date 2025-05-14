package com.sylviavitoria.api_votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Dados de resposta de um associado")
public class AssociadoResponse {

    @Schema(description = "Nome completo do associado", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do associado", example = "joao@exemplo.com")
    private String email;

    @Schema(description = "CPF do associado", example = "12345678901")
    private String cpf;
}
