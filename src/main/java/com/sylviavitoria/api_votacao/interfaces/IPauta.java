package com.sylviavitoria.api_votacao.interfaces;

import org.springframework.data.domain.Page;

import com.sylviavitoria.api_votacao.dto.PautaAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;

public interface IPauta {
    PautaResponse criar(PautaRequest request);
    PautaResponse buscarPorId(Long id);
    Page<PautaResponse> listarTodos(int page, int size, String sort);
    PautaResponse atualizar(Long id, PautaAtualizarRequest request);
    void deletar(Long id);
}