package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = """
        Dados para atualização do período de uma sessão de votação.

        Permite dois formatos de requisição:

        1. Adicionar Minutos:
        ```json
        {
          "minutosAdicionais": 30
        }
        ```

        2. Nova Data Fim:
        ```json
        {
          "dataFim": "2025-05-27T11:00:00"
        }
        ```
        """)
public class SessaoVotacaoAtualizarRequest {

    @Schema(description = "Nova data e hora de fechamento", example = "2025-05-26T11:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataFim;

    @Schema(description = "Minutos a serem adicionados ao tempo atual", example = "30")
    @Min(value = 1, message = "O número de minutos adicionais deve ser maior que zero")
    private Integer minutosAdicionais;
}