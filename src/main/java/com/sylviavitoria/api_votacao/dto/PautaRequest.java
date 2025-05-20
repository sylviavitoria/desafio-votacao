package com.sylviavitoria.api_votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para criação de uma nova pauta")
public class PautaRequest {
    
    @Schema(description = "Título da pauta", example = "Assembleia Geral 2025")
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @Schema(description = "Descrição detalhada da pauta", example = "Discussão sobre os resultados financeiros de 2024")
    private String descricao;
    
    @Schema(description = "ID do associado que está criando a pauta", example = "1")
    @NotNull(message = "O ID do criador é obrigatório")
    private Long criadorId;
}