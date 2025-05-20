package com.sylviavitoria.api_votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.exception.GlobalExceptionHandler;
import com.sylviavitoria.api_votacao.interfaces.IAssociado;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityExistsException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ AssociadoController.class, GlobalExceptionHandler.class })
public class AssociadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAssociado iAssociado;

    @Autowired
    private ObjectMapper objectMapper;

    private AssociadoRequest associadoRequest;
    private AssociadoResponse associadoResponse;
    private AssociadoListarResponse associadoListarResponse;

    @BeforeEach
    void setUp() {

        associadoRequest = new AssociadoRequest();
        associadoRequest.setNome("João da Silva");
        associadoRequest.setCpf("12345678901");
        associadoRequest.setEmail("joao@exemplo.com");

        associadoResponse = AssociadoResponse.builder()
                .nome("João da Silva")
                .cpf("12345678901")
                .email("joao@exemplo.com")
                .build();

        associadoListarResponse = AssociadoListarResponse.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao@exemplo.com")
                .build();
    }

    @Test
    @DisplayName("Deve criar um associado com sucesso")
    void criarAssociadoSucesso() throws Exception {
       
        when(iAssociado.criar(associadoRequest)).thenReturn(associadoResponse);

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(associadoResponse.getNome()))
                .andExpect(jsonPath("$.cpf").value(associadoResponse.getCpf()))
                .andExpect(jsonPath("$.email").value(associadoResponse.getEmail()));

        verify(iAssociado).criar(associadoRequest);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve buscar associado por ID com sucesso")
    void buscarPorIdSucesso() throws Exception {
        
        Long id = 1L;
        when(iAssociado.buscarPorId(id)).thenReturn(associadoResponse);

        mockMvc.perform(get("/api/v1/associados/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(associadoResponse.getNome()))
                .andExpect(jsonPath("$.cpf").value(associadoResponse.getCpf()))
                .andExpect(jsonPath("$.email").value(associadoResponse.getEmail()));

        verify(iAssociado).buscarPorId(id);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve retornar not found quando buscar associado com ID inexistente")
    void buscarPorIdInexistente() throws Exception {
        
        Long id = 99L;
        when(iAssociado.buscarPorId(id))
                .thenThrow(new EntityNotFoundException("Associado não encontrado com ID: " + id));

        mockMvc.perform(get("/api/v1/associados/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Associado não encontrado com ID: " + id));

        verify(iAssociado).buscarPorId(id);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve listar todos os associados com sucesso")
    void listarTodosSucesso() throws Exception {

        int page = 0;
        int size = 10;
        String sort = "nome";
        Page<AssociadoListarResponse> pageResponse = new PageImpl<>(List.of(associadoListarResponse));
        when(iAssociado.listarTodos(page, size, sort)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/associados")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(associadoListarResponse.getId()))
                .andExpect(jsonPath("$.content[0].nome").value(associadoListarResponse.getNome()))
                .andExpect(jsonPath("$.content[0].email").value(associadoListarResponse.getEmail()));

        verify(iAssociado).listarTodos(page, size, sort);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve atualizar um associado com sucesso")
    void atualizarAssociadoSucesso() throws Exception {

        Long id = 1L;
        AssociadoRequest requestAtualizado = new AssociadoRequest();
        requestAtualizado.setNome("João da Silva Atualizado");
        requestAtualizado.setCpf("12345678901");
        requestAtualizado.setEmail("joao.atualizado@exemplo.com");

        AssociadoResponse responseAtualizado = AssociadoResponse.builder()
                .nome(requestAtualizado.getNome())
                .cpf(requestAtualizado.getCpf())
                .email(requestAtualizado.getEmail())
                .build();

        when(iAssociado.atualizar(id, requestAtualizado)).thenReturn(responseAtualizado);

        mockMvc.perform(put("/api/v1/associados/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andExpect(status().isOk());

        verify(iAssociado).atualizar(id, requestAtualizado);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve deletar um associado com sucesso")
    void deletarAssociadoSucesso() throws Exception {

        Long id = 1L;
        doNothing().when(iAssociado).deletar(id);

        mockMvc.perform(delete("/api/v1/associados/{id}", id))
                .andExpect(status().isNoContent());

        verify(iAssociado).deletar(id);
        verifyNoMoreInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve retornar bad request quando criar associado com dados inválidos")
    void criarAssociadoDadosInvalidos() throws Exception {

        AssociadoRequest invalido = new AssociadoRequest();

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(iAssociado);
    }

    @Test
    @DisplayName("Deve retornar conflict quando criar associado com CPF existente")
    void criarAssociadoCpfExistente() throws Exception {

        when(iAssociado.criar(associadoRequest)).thenThrow(
                new EntityExistsException("Já existe um associado com este CPF"));

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Já existe um associado com este CPF"));

        verify(iAssociado).criar(associadoRequest);
        verifyNoMoreInteractions(iAssociado);
    }
}