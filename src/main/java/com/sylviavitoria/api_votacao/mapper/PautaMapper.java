package com.sylviavitoria.api_votacao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.sylviavitoria.api_votacao.dto.AssociadoDTO;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.repository.VotoRepository;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PautaMapper {
    
    @Autowired
    protected VotoRepository votoRepository;
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "criador", ignore = true)
    public abstract Pauta toEntity(PautaRequest request);
    
    @Mapping(target = "criador.id", source = "criador.id")
    @Mapping(target = "criador.nome", source = "criador.nome")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "totalVotosSim", expression = "java(getVotosSim(pauta))")
    @Mapping(target = "totalVotosNao", expression = "java(getVotosNao(pauta))")
    public abstract PautaResponse toResponse(Pauta pauta);
    
    protected Long getVotosSim(Pauta pauta) {
        return votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM);
    }
    
    protected Long getVotosNao(Pauta pauta) {
        return votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO);
    }
    
    protected AssociadoDTO toAssociadoDTO(Associado associado) {
        if (associado == null) {
            return null;
        }
        return AssociadoDTO.builder()
                .id(associado.getId())
                .nome(associado.getNome())
                .build();
    }
}