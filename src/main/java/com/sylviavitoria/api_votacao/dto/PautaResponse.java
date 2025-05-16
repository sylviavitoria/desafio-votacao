package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.sylviavitoria.api_votacao.enums.StatusPauta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Dados de resposta de uma pauta")
public class PautaResponse {
    
    @Schema(description = "ID da pauta", example = "1")
    private Long id;
    
    @Schema(description = "Título da pauta", example = "Assembleia Geral 2025")
    private String titulo;
    
    @Schema(description = "Descrição detalhada da pauta", example = "Discussão sobre os resultados financeiros de 2025")
    private String descricao;
    
    @Schema(description = "Data e hora de criação da pauta", example = "2025-05-15T10:30:00")
    private LocalDateTime dataCriacao;
    
    @Schema(description = "Status atual da pauta", example = "CRIADA", allowableValues = {"CRIADA", "EM_VOTACAO", "ENCERRADA"})
    private StatusPauta status;
    
    @Schema(description = "Informações do criador da pauta")
    private AssociadoDTO criador;
}