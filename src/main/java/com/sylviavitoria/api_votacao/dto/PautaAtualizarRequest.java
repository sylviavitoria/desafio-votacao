package com.sylviavitoria.api_votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para atualização de uma pauta")
public class PautaAtualizarRequest {
    
    @Schema(description = "Título da pauta", example = "Assembleia Geral 2025 - Atualizada")
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @Schema(description = "Descrição detalhada da pauta", example = "Discussão sobre os resultados financeiros de 2025 e planejamento para 2026")
    private String descricao;
}