package com.sylviavitoria.api_votacao.interfaces;

import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;

public interface IVoto {
    VotoResponse registrarVoto(VotoRequest request);
    ResultadoVotacaoResponse consultarResultado(Long pautaId);
}