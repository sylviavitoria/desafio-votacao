package com.sylviavitoria.api_votacao.interfaces;

import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;


public interface IVoto {
    VotoResponse registrarVoto(VotoRequest request);
    VotoResponse buscarPorId(Long id); 
    ResultadoVotacaoResponse consultarResultado(Long pautaId);
    VotoResponse atualizarVoto(Long id, VotoAtualizarRequest request);
}