package com.sylviavitoria.api_votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Dados de resposta de um associado")
public class AssociadoDTO {

    @Schema(description = "ID do associado", example = "1")
    private Long id;
    
    @Schema(description = "Nome do associado", example = "João Silva")
    private String nome;
}