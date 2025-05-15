package com.sylviavitoria.api_votacao.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociadoDTO {
    
    private Long id;
    
    private String nome;
}