package com.sylviavitoria.api_votacao.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;

public interface IAssociado {
    AssociadoResponse criar(AssociadoRequest request);
    AssociadoResponse buscarPorId(Long id);
    Page<AssociadoListarResponse> listarTodos(Pageable pageable);
    AssociadoResponse atualizar(Long id, AssociadoRequest request);
    void deletar(Long id);
}