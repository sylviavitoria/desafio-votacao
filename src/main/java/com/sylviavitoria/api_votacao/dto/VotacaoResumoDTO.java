package com.sylviavitoria.api_votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VotacaoResumoDTO {
    private final Long totalVotosSim;
    private final Long totalVotosNao;
}