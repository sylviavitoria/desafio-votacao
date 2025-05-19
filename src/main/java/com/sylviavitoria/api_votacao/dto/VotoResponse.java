package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.sylviavitoria.api_votacao.enums.OpcaoVoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotoResponse {
    
    private Long id;
    
    private OpcaoVoto opcao;
    
    private LocalDateTime dataHora;
    
    private Long associadoId;
    
    private String associadoNome;

    private Long pautaId;
    
    private String pautaTitulo;
}