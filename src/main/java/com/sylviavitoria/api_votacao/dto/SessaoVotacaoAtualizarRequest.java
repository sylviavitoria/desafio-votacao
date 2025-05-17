package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para atualização do período de uma sessão de votação")
public class SessaoVotacaoAtualizarRequest {
    
    @Schema(description = "Nova data e hora de fechamento", example = "2025-05-16T19:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataFim;
    
    @Schema(description = "Minutos a serem adicionados ao tempo atual", example = "30")
    @Min(value = 1, message = "O número de minutos adicionais deve ser maior que zero")
    private Integer minutosAdicionais;
}