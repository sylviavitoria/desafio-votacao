package com.sylviavitoria.api_votacao.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociadoListarResponse {
    private Long id;
    private String nome;
    private String email;
}