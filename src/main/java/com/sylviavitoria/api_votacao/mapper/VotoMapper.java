package com.sylviavitoria.api_votacao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sylviavitoria.api_votacao.dto.VotoResponse;
import com.sylviavitoria.api_votacao.model.Voto;

@Mapper(componentModel = "spring")
public interface VotoMapper {
    
    @Mapping(target = "associadoId", source = "associado.id")
    @Mapping(target = "associadoNome", source = "associado.nome")
    @Mapping(target = "pautaId", source = "pauta.id")
    @Mapping(target = "pautaTitulo", source = "pauta.titulo")
    VotoResponse toResponse(Voto voto);
}