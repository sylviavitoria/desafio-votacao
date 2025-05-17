package com.sylviavitoria.api_votacao.interfaces;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;

import org.springframework.data.domain.Page;

public interface ISessaoVotacao {
    SessaoVotacaoResponse criar(SessaoVotacaoRequest request);
    SessaoVotacaoResponse buscarPorId(Long id);
    Page<SessaoVotacaoResponse> listarTodos(int page, int size, String sort);
    SessaoVotacaoResponse atualizarPeriodo(Long id, SessaoVotacaoAtualizarRequest request);
}