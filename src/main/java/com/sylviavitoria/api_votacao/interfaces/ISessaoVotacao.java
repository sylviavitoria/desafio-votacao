package com.sylviavitoria.api_votacao.interfaces;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;

public interface ISessaoVotacao {
    SessaoVotacaoResponse criar(SessaoVotacaoRequest request);
    SessaoVotacaoResponse buscarPorId(Long id);
}