package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sylviavitoria.api_votacao.enums.StatusSessao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacaoResponse {
    
    private Long id;
    
    private Long pautaId;
    
    private String pautaTitulo;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAbertura;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataFechamento;
    
    private StatusSessao status;
    
    private boolean abertaParaVotacao;
}