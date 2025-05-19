package com.sylviavitoria.api_votacao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoVotacaoResponse {
    
    private Long pautaId;
    
    private String pautaTitulo;
    
    private long votosSim;
    
    private long votosNao;
    
    private long totalVotos;
}