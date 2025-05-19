package com.sylviavitoria.api_votacao.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoAtualizarRequest;
import com.sylviavitoria.api_votacao.enums.StatusSessao;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.exception.GlobalExceptionHandler;
import com.sylviavitoria.api_votacao.interfaces.ISessaoVotacao;

@WebMvcTest({ SessaoVotacaoController.class, GlobalExceptionHandler.class })
class SessaoVotacaoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ISessaoVotacao sessaoVotacao;

        private SessaoVotacaoRequest sessaoRequest;
        private SessaoVotacaoResponse sessaoResponse;
        private LocalDateTime agora;

        @BeforeEach
        void setUp() {
                agora = LocalDateTime.now();

                sessaoRequest = new SessaoVotacaoRequest();
                sessaoRequest.setPautaId(1L);
                sessaoRequest.setDataInicio(agora.plusMinutes(5));
                sessaoRequest.setDataFim(agora.plusMinutes(10));

                sessaoResponse = SessaoVotacaoResponse.builder()
                                .id(1L)
                                .pautaId(1L)
                                .pautaTitulo("Pauta Teste")
                                .dataAbertura(agora.plusMinutes(5))
                                .dataFechamento(agora.plusMinutes(10))
                                .status(StatusSessao.FECHADA)
                                .abertaParaVotacao(false)
                                .build();

        }

        @Test
        @DisplayName("Deve criar sessão com sucesso")
        void criarSessaoSucesso() throws Exception {
              
                ArgumentCaptor<SessaoVotacaoRequest> requestCaptor = ArgumentCaptor
                                .forClass(SessaoVotacaoRequest.class);

                when(sessaoVotacao.criar(requestCaptor.capture())).thenReturn(sessaoResponse);

                mockMvc.perform(post("/api/v1/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sessaoRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(sessaoResponse.getId()))
                                .andExpect(jsonPath("$.pautaId").value(sessaoResponse.getPautaId()))
                                .andExpect(jsonPath("$.status").value(sessaoResponse.getStatus().toString()));

                SessaoVotacaoRequest capturedRequest = requestCaptor.getValue();
                assertEquals(sessaoRequest.getPautaId(), capturedRequest.getPautaId());

                assertTrue(sessaoRequest.getDataInicio().withNano(0)
                                .isEqual(capturedRequest.getDataInicio().withNano(0)),
                                "Data início deve ser igual ignorando nanossegundos");

                assertTrue(sessaoRequest.getDataFim().withNano(0)
                                .isEqual(capturedRequest.getDataFim().withNano(0)),
                                "Data fim deve ser igual ignorando nanossegundos");

                verify(sessaoVotacao).criar(argThat(request -> request.getPautaId().equals(sessaoRequest.getPautaId())
                                &&
                                request.getDataInicio().withNano(0).isEqual(sessaoRequest.getDataInicio().withNano(0))
                                &&
                                request.getDataFim().withNano(0).isEqual(sessaoRequest.getDataFim().withNano(0))));
                verifyNoMoreInteractions(sessaoVotacao);
        }

        @Test
        @DisplayName("Deve retornar bad request quando request inválido")
        void criarSessaoRequestInvalido() throws Exception {
                SessaoVotacaoRequest requestInvalido = new SessaoVotacaoRequest();

                mockMvc.perform(post("/api/v1/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInvalido)))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(sessaoVotacao);
        }

        @Test
        @DisplayName("Deve consultar sessão por ID com sucesso")
        void consultarSessaoSucesso() throws Exception {
                Long id = 1L;
                when(sessaoVotacao.buscarPorId(id)).thenReturn(sessaoResponse);

                mockMvc.perform(get("/api/v1/sessoes/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(sessaoResponse.getId()))
                                .andExpect(jsonPath("$.pautaId").value(sessaoResponse.getPautaId()))
                                .andExpect(jsonPath("$.status").value(sessaoResponse.getStatus().toString()));

                verify(sessaoVotacao).buscarPorId(id);
                verifyNoMoreInteractions(sessaoVotacao);
        }

        @Test
        @DisplayName("Deve retornar not found quando sessão não existe")
        void consultarSessaoNaoExistente() throws Exception {
                Long id = 99L;
                when(sessaoVotacao.buscarPorId(id)).thenThrow(new EntityNotFoundException("Sessão não encontrada"));

                mockMvc.perform(get("/api/v1/sessoes/{id}", id))
                                .andExpect(status().isNotFound());

                verify(sessaoVotacao).buscarPorId(id);
                verifyNoMoreInteractions(sessaoVotacao);
        }

        @Test
        @DisplayName("Deve listar todas as sessões com sucesso")
        void listarTodosSucesso() throws Exception {
                int page = 0;
                int size = 10;
                String sort = "dataAbertura";

                when(sessaoVotacao.listarTodos(page, size, sort))
                                .thenReturn(new PageImpl<>(List.of(sessaoResponse)));

                mockMvc.perform(get("/api/v1/sessoes")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sort", sort))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(sessaoResponse.getId()))
                                .andExpect(jsonPath("$.content[0].pautaId").value(sessaoResponse.getPautaId()))
                                .andExpect(jsonPath("$.content[0].status")
                                                .value(sessaoResponse.getStatus().toString()));

                verify(sessaoVotacao).listarTodos(page, size, sort);
                verifyNoMoreInteractions(sessaoVotacao);
        }

        @Test
        @DisplayName("Deve atualizar período da sessão com sucesso")
        void atualizarPeriodoSucesso() throws Exception {
                
                Long id = 1L;
                LocalDateTime novaDataFim = agora.plusHours(1).withNano(0); 

                SessaoVotacaoAtualizarRequest atualizarRequest = new SessaoVotacaoAtualizarRequest();
                atualizarRequest.setDataFim(novaDataFim);

                SessaoVotacaoResponse responseAtualizado = SessaoVotacaoResponse.builder()
                                .id(1L)
                                .pautaId(1L)
                                .pautaTitulo("Pauta Teste")
                                .dataAbertura(agora.withNano(0))
                                .dataFechamento(novaDataFim)
                                .status(StatusSessao.ABERTA)
                                .abertaParaVotacao(true)
                                .build();

                ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
                ArgumentCaptor<SessaoVotacaoAtualizarRequest> requestCaptor = ArgumentCaptor
                                .forClass(SessaoVotacaoAtualizarRequest.class);

                when(sessaoVotacao.atualizarPeriodo(idCaptor.capture(), requestCaptor.capture()))
                                .thenReturn(responseAtualizado);

                mockMvc.perform(put("/api/v1/sessoes/{id}/periodo", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(atualizarRequest)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(responseAtualizado.getId()))
                                .andExpect(jsonPath("$.pautaId").value(responseAtualizado.getPautaId()))
                                .andExpect(jsonPath("$.pautaTitulo").value(responseAtualizado.getPautaTitulo()))
                                .andExpect(jsonPath("$.status").value(responseAtualizado.getStatus().toString()))
                                .andExpect(jsonPath("$.abertaParaVotacao")
                                                .value(responseAtualizado.isAbertaParaVotacao()));

                assertEquals(id, idCaptor.getValue());

                LocalDateTime capturedDataFim = requestCaptor.getValue().getDataFim();
                assertTrue(novaDataFim.isEqual(capturedDataFim.withNano(0)),
                                "Datas devem ser iguais ignorando nanossegundos");

                verify(sessaoVotacao).atualizarPeriodo(eq(id),
                                argThat(request -> request.getDataFim().withNano(0).isEqual(novaDataFim)));
                verifyNoMoreInteractions(sessaoVotacao);
        }

}