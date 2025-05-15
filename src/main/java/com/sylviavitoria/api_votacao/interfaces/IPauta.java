package com.sylviavitoria.api_votacao.interfaces;

import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;

public interface IPauta {
    PautaResponse criar(PautaRequest request);
    PautaResponse buscarPorId(Long id);
    
}