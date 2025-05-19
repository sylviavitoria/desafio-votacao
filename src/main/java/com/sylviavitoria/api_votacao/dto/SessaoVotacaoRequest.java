package com.sylviavitoria.api_votacao.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "SessaoVotacaoRequest",
        description = """
                Dados para criação de uma sessão de votação.
                
                Permite dois formatos de requisição:
                
                1. Abertura Imediata:
                ```json
                {
                  "pautaId": 1,
                  "duracaoMinutos": 5
                }
                ```
                
                2. Agendamento:
                ```json
                {
                  "pautaId": 1,
                  "dataInicio": "2025-05-25T22:00:00",
                  "dataFim": "2025-05-26T11:00:00"
                }
                ```
                
                - Se não informar duracaoMinutos, será usado 1 minuto por default
                - Para agendamento, é obrigatório informar dataInicio e dataFim
                """)
public class SessaoVotacaoRequest {
    
    @Schema(description = "ID da pauta", example = "1", required = true)
    @NotNull(message = "O ID da pauta é obrigatório")
    private Long pautaId;
    
    @Schema(description = "Duração em minutos (usado para abertura imediata)", 
           example = "5",
           defaultValue = "1")
    private Integer duracaoMinutos;
    
@Schema(description = "Data e hora de início (usado para agendamento)", 
       example = "2025-05-25T22:00:00",
       type = "string")
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime dataInicio;

@Schema(description = "Data e hora de fim (usado para agendamento)", 
       example = "2025-05-26T11:00:00",
       type = "string")
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime dataFim;
}