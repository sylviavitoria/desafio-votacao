package com.sylviavitoria.api_votacao.mapper;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.model.Associado;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssociadoMapper {
    
    Associado toEntity(AssociadoRequest request);
    
    AssociadoResponse toResponse(Associado associado);

    AssociadoListarResponse toListarResponse(Associado associado);
    
    List<AssociadoListarResponse> toListResponse(List<Associado> associados);
}