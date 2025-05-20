package com.sylviavitoria.api_votacao.dto;

import com.sylviavitoria.api_votacao.enums.OpcaoVoto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VotoRequest {
    
    @NotNull(message = "O ID do associado é obrigatório")
    private Long associadoId;
    
    @NotNull(message = "O ID da pauta é obrigatório")
    private Long pautaId;
    
    @NotNull(message = "A opção de voto é obrigatória")
    private OpcaoVoto opcao;
}