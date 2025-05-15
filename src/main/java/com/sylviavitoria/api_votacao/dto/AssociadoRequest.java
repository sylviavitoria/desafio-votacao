package com.sylviavitoria.api_votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
@Schema(description = "Dados para criação ou atualização de um associado")
public class AssociadoRequest {

   @Schema(description = "Nome completo do associado", example = "João da Silva")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "CPF do associado (apenas números)", example = "12345678901")
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @Schema(description = "Email do associado", example = "joao@exemplo.com")
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
}