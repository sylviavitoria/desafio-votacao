package com.sylviavitoria.api_votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.api_votacao.dto.AssociadoDTO;
import com.sylviavitoria.api_votacao.dto.PautaAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.exception.GlobalExceptionHandler;
import com.sylviavitoria.api_votacao.interfaces.IPauta;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ PautaController.class, GlobalExceptionHandler.class })
public class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPauta iPauta;

    @Autowired
    private ObjectMapper objectMapper;

    private PautaRequest pautaRequest;
    private PautaResponse pautaResponse;
    private PautaAtualizarRequest pautaAtualizarRequest;
    private LocalDateTime dataCriacao;

    @BeforeEach
    void setUp() {
        dataCriacao = LocalDateTime.now();

        pautaRequest = new PautaRequest();
        pautaRequest.setTitulo("Assembleia Geral 2025");
        pautaRequest.setDescricao("Discussão sobre os resultados financeiros de 2024");
        pautaRequest.setCriadorId(1L);

        AssociadoDTO criador = AssociadoDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .build();

        pautaResponse = PautaResponse.builder()
                .id(1L)
                .titulo("Assembleia Geral 2025")
                .descricao("Discussão sobre os resultados financeiros de 2024")
                .dataCriacao(dataCriacao)
                .status(StatusPauta.CRIADA)
                .criador(criador)
                .build();

        pautaAtualizarRequest = new PautaAtualizarRequest();
        pautaAtualizarRequest.setTitulo("Assembleia Geral 2025 - Atualizada");
        pautaAtualizarRequest.setDescricao("Discussão atualizada sobre os resultados financeiros de 2024");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PautaController(iPauta))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso")
    void criarPautaSucesso() throws Exception {

        ArgumentCaptor<PautaRequest> requestCaptor = ArgumentCaptor.forClass(PautaRequest.class);

        when(iPauta.criar(requestCaptor.capture())).thenReturn(pautaResponse);

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        PautaRequest capturedRequest = requestCaptor.getValue();
        assertEquals(pautaRequest.getTitulo(), capturedRequest.getTitulo());
        assertEquals(pautaRequest.getDescricao(), capturedRequest.getDescricao());
        assertEquals(pautaRequest.getCriadorId(), capturedRequest.getCriadorId());

        verify(iPauta).criar(capturedRequest);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar not found quando criar pauta com criador inexistente")
    void criarPautaCriadorInexistente() throws Exception {

        when(iPauta.criar(argThat(request -> request.getTitulo().equals(pautaRequest.getTitulo()) &&
                request.getDescricao().equals(pautaRequest.getDescricao()) &&
                request.getCriadorId().equals(pautaRequest.getCriadorId())))).thenThrow(
                        new EntityNotFoundException("Associado não encontrado com ID: " + pautaRequest.getCriadorId()));

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Associado não encontrado com ID: " + pautaRequest.getCriadorId()));

        verify(iPauta).criar(argThat(request -> request.getTitulo().equals(pautaRequest.getTitulo()) &&
                request.getDescricao().equals(pautaRequest.getDescricao()) &&
                request.getCriadorId().equals(pautaRequest.getCriadorId())));
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve buscar pauta por ID com sucesso")
    void buscarPautaPorIdSucesso() throws Exception {
        Long id = 1L;
        when(iPauta.buscarPorId(id)).thenReturn(pautaResponse);

        mockMvc.perform(get("/api/v1/pautas/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(iPauta).buscarPorId(id);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar not found quando buscar pauta com ID inexistente")
    void buscarPautaPorIdInexistente() throws Exception {
        Long id = 99L;
        when(iPauta.buscarPorId(id)).thenThrow(new EntityNotFoundException("Pauta não encontrada com ID: " + id));

        mockMvc.perform(get("/api/v1/pautas/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Pauta não encontrada com ID: " + id));

        verify(iPauta).buscarPorId(id);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve listar todas as pautas com sucesso")
    void listarTodasPautasSucesso() throws Exception {
        int page = 0;
        int size = 10;
        String sort = "titulo";

        List<PautaResponse> content = List.of(pautaResponse);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<PautaResponse> pageResponse = new PageImpl<>(content, pageable, content.size());

        when(iPauta.listarTodos(page, size, sort)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/pautas")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort))
                .andDo(print())
                .andExpect(status().isOk());

        verify(iPauta).listarTodos(page, size, sort);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso")
    void atualizarPautaSucesso() throws Exception {
        Long id = 1L;
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PautaAtualizarRequest> requestCaptor = ArgumentCaptor.forClass(PautaAtualizarRequest.class);

        PautaResponse responseAtualizado = PautaResponse.builder()
                .id(id)
                .titulo(pautaAtualizarRequest.getTitulo())
                .descricao(pautaAtualizarRequest.getDescricao())
                .dataCriacao(dataCriacao)
                .status(StatusPauta.CRIADA)
                .criador(pautaResponse.getCriador())
                .build();

        when(iPauta.atualizar(idCaptor.capture(), requestCaptor.capture())).thenReturn(responseAtualizado);

        mockMvc.perform(put("/api/v1/pautas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaAtualizarRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(id, idCaptor.getValue());
        assertEquals(pautaAtualizarRequest.getTitulo(), requestCaptor.getValue().getTitulo());
        assertEquals(pautaAtualizarRequest.getDescricao(), requestCaptor.getValue().getDescricao());

        verify(iPauta).atualizar(eq(id),
                argThat(request -> request.getTitulo().equals(pautaAtualizarRequest.getTitulo()) &&
                        request.getDescricao().equals(pautaAtualizarRequest.getDescricao())));
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar not found quando atualizar pauta inexistente")
    void atualizarPautaInexistente() throws Exception {
        Long id = 99L;

        when(iPauta.atualizar(eq(id),
                argThat(request -> request.getTitulo().equals(pautaAtualizarRequest.getTitulo()) &&
                        request.getDescricao().equals(pautaAtualizarRequest.getDescricao()))))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada com ID: " + id));

        mockMvc.perform(put("/api/v1/pautas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaAtualizarRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Pauta não encontrada com ID: " + id));

        verify(iPauta).atualizar(eq(id),
                argThat(request -> request.getTitulo().equals(pautaAtualizarRequest.getTitulo()) &&
                        request.getDescricao().equals(pautaAtualizarRequest.getDescricao())));
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar conflict quando tentar atualizar pauta em votação")
    void atualizarPautaEmVotacao() throws Exception {
        Long id = 1L;

        when(iPauta.atualizar(eq(id),
                argThat(request -> request.getTitulo().equals(pautaAtualizarRequest.getTitulo()) &&
                        request.getDescricao().equals(pautaAtualizarRequest.getDescricao()))))
                .thenThrow(
                        new BusinessException("Não é possível editar uma pauta que já está em votação ou encerrada"));

        mockMvc.perform(put("/api/v1/pautas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaAtualizarRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Não é possível editar uma pauta que já está em votação ou encerrada"));

        verify(iPauta).atualizar(eq(id),
                argThat(request -> request.getTitulo().equals(pautaAtualizarRequest.getTitulo()) &&
                        request.getDescricao().equals(pautaAtualizarRequest.getDescricao())));
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve deletar uma pauta com sucesso")
    void deletarPautaSucesso() throws Exception {
        Long id = 1L;
        doNothing().when(iPauta).deletar(id);

        mockMvc.perform(delete("/api/v1/pautas/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(iPauta).deletar(id);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar not found quando deletar pauta inexistente")
    void deletarPautaInexistente() throws Exception {
        Long id = 99L;
        doThrow(new EntityNotFoundException("Pauta não encontrada com ID: " + id))
                .when(iPauta).deletar(id);

        mockMvc.perform(delete("/api/v1/pautas/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Pauta não encontrada com ID: " + id));

        verify(iPauta).deletar(id);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar conflict quando deletar pauta em votação")
    void deletarPautaEmVotacao() throws Exception {
        Long id = 1L;
        doThrow(new IllegalStateException("Não é possível deletar uma pauta que está em votação"))
                .when(iPauta).deletar(id);

        mockMvc.perform(delete("/api/v1/pautas/{id}", id))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Não é possível deletar uma pauta que está em votação"));

        verify(iPauta).deletar(id);
        verifyNoMoreInteractions(iPauta);
    }

    @Test
    @DisplayName("Deve retornar bad request quando criar pauta com dados inválidos")
    void criarPautaDadosInvalidos() throws Exception {
        PautaRequest invalido = new PautaRequest();

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(iPauta);
    }
}