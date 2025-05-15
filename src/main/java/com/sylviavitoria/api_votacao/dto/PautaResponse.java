package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.sylviavitoria.api_votacao.enums.StatusPauta;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PautaResponse {
    
    private Long id;
    
    private String titulo;
    
    private String descricao;
    
    private LocalDateTime dataCriacao;
    
    private StatusPauta status;
    
    private AssociadoDTO criador;
}