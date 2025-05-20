package com.sylviavitoria.api_votacao.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.exception.GlobalExceptionHandler;
import com.sylviavitoria.api_votacao.interfaces.IVoto;

@WebMvcTest({VotoController.class, GlobalExceptionHandler.class})
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IVoto votoService;

    private VotoRequest votoRequest;
    private VotoResponse votoResponse;
    private ResultadoVotacaoResponse resultadoResponse;

    @BeforeEach
    void setUp() {
        votoRequest = new VotoRequest();
        votoRequest.setAssociadoId(1L);
        votoRequest.setPautaId(1L);
        votoRequest.setOpcao(OpcaoVoto.SIM);

        votoResponse = VotoResponse.builder()
                .id(1L)
                .associadoId(1L)
                .pautaId(1L)
                .opcao(OpcaoVoto.SIM)
                .build();

        resultadoResponse = ResultadoVotacaoResponse.builder()
                .pautaId(1L)
                .pautaTitulo("Pauta Teste")
                .votosSim(3L)
                .votosNao(2L)
                .totalVotos(5L)
                .build();
    }

    @Test
    @DisplayName("Deve registrar voto com sucesso")
    void registrarVotoSucesso() throws Exception {
        
        ArgumentCaptor<VotoRequest> requestCaptor = ArgumentCaptor.forClass(VotoRequest.class);
        when(votoService.registrarVoto(requestCaptor.capture())).thenReturn(votoResponse);

        mockMvc.perform(post("/api/v1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(votoResponse.getId()))
                .andExpect(jsonPath("$.associadoId").value(votoResponse.getAssociadoId()))
                .andExpect(jsonPath("$.pautaId").value(votoResponse.getPautaId()))
                .andExpect(jsonPath("$.opcao").value(votoResponse.getOpcao().toString()));

        verify(votoService).registrarVoto(argThat(request -> 
            request.getAssociadoId().equals(votoRequest.getAssociadoId()) &&
            request.getPautaId().equals(votoRequest.getPautaId()) &&
            request.getOpcao().equals(votoRequest.getOpcao())
        ));
        verifyNoMoreInteractions(votoService);
    }

    @Test
    @DisplayName("Deve buscar voto por ID com sucesso")
    void buscarVotoPorIdSucesso() throws Exception {
        Long id = 1L;
        when(votoService.buscarPorId(id)).thenReturn(votoResponse);

        mockMvc.perform(get("/api/v1/votos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(votoResponse.getId()))
                .andExpect(jsonPath("$.associadoId").value(votoResponse.getAssociadoId()))
                .andExpect(jsonPath("$.pautaId").value(votoResponse.getPautaId()))
                .andExpect(jsonPath("$.opcao").value(votoResponse.getOpcao().toString()));

        verify(votoService).buscarPorId(id);
        verifyNoMoreInteractions(votoService);
    }

    @Test
    @DisplayName("Deve retornar not found quando voto não existe")
    void buscarVotoInexistente() throws Exception {

        Long id = 99L;
        when(votoService.buscarPorId(id))
                .thenThrow(new EntityNotFoundException("Voto não encontrado"));

        mockMvc.perform(get("/api/v1/votos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Voto não encontrado"));

        verify(votoService).buscarPorId(id);
        verifyNoMoreInteractions(votoService);
    }

    @Test
    @DisplayName("Deve consultar resultado da votação com sucesso")
    void consultarResultadoSucesso() throws Exception {
        
        Long pautaId = 1L;
        when(votoService.consultarResultado(pautaId)).thenReturn(resultadoResponse);

        mockMvc.perform(get("/api/v1/votos/pautas/{pautaId}/resultado", pautaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(resultadoResponse.getPautaId()))
                .andExpect(jsonPath("$.pautaTitulo").value(resultadoResponse.getPautaTitulo()))
                .andExpect(jsonPath("$.votosSim").value(resultadoResponse.getVotosSim()))
                .andExpect(jsonPath("$.votosNao").value(resultadoResponse.getVotosNao()))
                .andExpect(jsonPath("$.totalVotos").value(resultadoResponse.getTotalVotos()));

        verify(votoService).consultarResultado(pautaId);
        verifyNoMoreInteractions(votoService);
    }

    @Test
    @DisplayName("Deve atualizar voto com sucesso")
    void atualizarVotoSucesso() throws Exception {

        Long id = 1L;
        VotoAtualizarRequest atualizarRequest = new VotoAtualizarRequest();
        atualizarRequest.setOpcao(OpcaoVoto.NAO);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<VotoAtualizarRequest> requestCaptor = ArgumentCaptor.forClass(VotoAtualizarRequest.class);

        when(votoService.atualizarVoto(idCaptor.capture(), requestCaptor.capture()))
                .thenReturn(votoResponse);

        mockMvc.perform(put("/api/v1/votos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(votoResponse.getId()))
                .andExpect(jsonPath("$.associadoId").value(votoResponse.getAssociadoId()))
                .andExpect(jsonPath("$.pautaId").value(votoResponse.getPautaId()))
                .andExpect(jsonPath("$.opcao").value(votoResponse.getOpcao().toString()));

        verify(votoService).atualizarVoto(eq(id), argThat(request -> 
            request.getOpcao().equals(atualizarRequest.getOpcao())
        ));
        verifyNoMoreInteractions(votoService);
    }

    @Test
    @DisplayName("Deve retornar conflict quando sessão encerrada")
    void atualizarVotoSessaoEncerrada() throws Exception {

        Long id = 1L;
        VotoAtualizarRequest atualizarRequest = new VotoAtualizarRequest();
        atualizarRequest.setOpcao(OpcaoVoto.NAO);

        when(votoService.atualizarVoto(eq(id), argThat(request -> 
            request.getOpcao().equals(atualizarRequest.getOpcao())
        ))).thenThrow(new BusinessException("Não é possível alterar um voto após o encerramento da sessão"));

        mockMvc.perform(put("/api/v1/votos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Não é possível alterar um voto após o encerramento da sessão"));

        verify(votoService).atualizarVoto(eq(id), argThat(request -> 
            request.getOpcao().equals(atualizarRequest.getOpcao())
        ));
        verifyNoMoreInteractions(votoService);
    }
}