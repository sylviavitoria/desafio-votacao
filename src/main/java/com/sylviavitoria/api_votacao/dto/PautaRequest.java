package com.sylviavitoria.api_votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PautaRequest {
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    private String descricao;
    
    @NotNull(message = "O ID do criador é obrigatório")
    private Long criadorId;
}