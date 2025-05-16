package com.sylviavitoria.api_votacao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PautaAtualizarRequest {
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
}