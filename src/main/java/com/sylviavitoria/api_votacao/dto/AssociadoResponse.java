package com.sylviavitoria.api_votacao.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociadoResponse {

    private String nome;

    private String email;

    private String cpf;
}
