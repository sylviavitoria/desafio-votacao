package com.sylviavitoria.api_votacao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessaoVotacaoMapper {
    
    @Mapping(target = "pautaId", source = "pauta.id")
    @Mapping(target = "pautaTitulo", source = "pauta.titulo")
    @Mapping(target = "abertaParaVotacao", expression = "java(sessao.estaAberta())")
    SessaoVotacaoResponse toResponse(SessaoVotacao sessao);
}