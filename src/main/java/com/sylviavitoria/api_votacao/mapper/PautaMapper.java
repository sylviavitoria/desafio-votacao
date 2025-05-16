package com.sylviavitoria.api_votacao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.sylviavitoria.api_votacao.dto.AssociadoDTO;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PautaMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "criador", ignore = true)
    Pauta toEntity(PautaRequest request);
    
    @Mapping(target = "criador.id", source = "criador.id")
    @Mapping(target = "criador.nome", source = "criador.nome")
    PautaResponse toResponse(Pauta pauta);
    
    default AssociadoDTO toAssociadoDTO(Associado associado) {
        if (associado == null) {
            return null;
        }
        return AssociadoDTO.builder()
                .id(associado.getId())
                .nome(associado.getNome())
                .build();
    }
}