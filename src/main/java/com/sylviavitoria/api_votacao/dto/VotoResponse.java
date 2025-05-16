package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VotoResponse {
    
    private Long id;
    
    private OpcaoVoto opcao;
    
    private LocalDateTime dataHora;
    
    private Long associadoId;
    
    private String associadoNome;

    private Long pautaId;
    
    private String pautaTitulo;
}