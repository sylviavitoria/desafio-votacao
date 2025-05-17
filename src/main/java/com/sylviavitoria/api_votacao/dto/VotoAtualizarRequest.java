package com.sylviavitoria.api_votacao.dto;

import com.sylviavitoria.api_votacao.enums.OpcaoVoto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Dados para atualização de um voto")
public class VotoAtualizarRequest {
    
    @NotNull(message = "A opção de voto é obrigatória")
    @Schema(description = "Nova opção de voto", example = "SIM", allowableValues = {"SIM", "NAO"})
    private OpcaoVoto opcao;
}