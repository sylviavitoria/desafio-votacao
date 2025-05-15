package com.sylviavitoria.api_votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)  
@AllArgsConstructor   
public class AssociadoListarResponse {
    private Long id;
    private String nome;
    private String email;
}