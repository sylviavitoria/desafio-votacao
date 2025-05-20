package com.sylviavitoria.api_votacao.service;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.mapper.AssociadoMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import com.sylviavitoria.api_votacao.repository.PautaRepository;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AsssociadoServiceTest {
    
    @Mock
    private AssociadoRepository associadoRepository;
    
    @Mock
    private AssociadoMapper associadoMapper;

    @Mock 
    private PautaRepository pautaRepository;
    
    @InjectMocks
    private AssociadoService associadoService;
    
    private AssociadoRequest associadoRequest;
    private Associado associado;
    private AssociadoResponse associadoResponse;
    private AssociadoListarResponse associadoListarResponse;
    
    @BeforeEach
    void setUp() {
        associadoRequest = new AssociadoRequest();
        associadoRequest.setNome("João da Silva");
        associadoRequest.setCpf("12345678901");
        associadoRequest.setEmail("joao@exemplo.com");
        
        associado = new Associado();
        associado.setId(1L);
        associado.setNome("João da Silva");
        associado.setCpf("12345678901");
        associado.setEmail("joao@exemplo.com");
        
        associadoResponse = AssociadoResponse.builder()
                .nome("João da Silva")
                .cpf("12345678901")
                .email("joao@exemplo.com")
                .build();
        
        associadoListarResponse = new AssociadoListarResponse();
    }
    
    @Test
    @DisplayName("Deve criar um associado com sucesso")
    void criarAssociadoSucesso() {

        when(associadoRepository.findByCpf(associadoRequest.getCpf())).thenReturn(Optional.empty());
        when(associadoMapper.toEntity(associadoRequest)).thenReturn(associado);
        when(associadoRepository.save(associado)).thenReturn(associado);
        when(associadoMapper.toResponse(associado)).thenReturn(associadoResponse);
        
        AssociadoResponse resultado = associadoService.criar(associadoRequest);
        
        assertNotNull(resultado);
        assertEquals(associadoRequest.getNome(), resultado.getNome());
        assertEquals(associadoRequest.getCpf(), resultado.getCpf());
        assertEquals(associadoRequest.getEmail(), resultado.getEmail());
        
        verify(associadoRepository).findByCpf(associadoRequest.getCpf());
        verify(associadoMapper).toEntity(associadoRequest);
        verify(associadoRepository).save(associado);
        verify(associadoMapper).toResponse(associado);
        verifyNoMoreInteractions(associadoRepository, associadoMapper);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao tentar criar associado com CPF existente")
    void criarAssociadoCpfExistente() {

        when(associadoRepository.findByCpf(associadoRequest.getCpf())).thenReturn(Optional.of(associado));
        
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            associadoService.criar(associadoRequest);
        });
        
        assertEquals("Já existe um associado com este CPF", exception.getMessage());
        verify(associadoRepository).findByCpf(associadoRequest.getCpf());
        verifyNoInteractions(associadoMapper);
        verifyNoMoreInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve buscar associado por ID com sucesso")
    void buscarPorIdSucesso() {

        Long id = 1L;
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoMapper.toResponse(associado)).thenReturn(associadoResponse);
        
        AssociadoResponse resultado = associadoService.buscarPorId(id);
        
        assertNotNull(resultado);
        assertEquals(associado.getNome(), resultado.getNome());
        assertEquals(associado.getCpf(), resultado.getCpf());
        assertEquals(associado.getEmail(), resultado.getEmail());
        
        verify(associadoRepository).findById(id);
        verify(associadoMapper).toResponse(associado);
        verifyNoMoreInteractions(associadoRepository, associadoMapper);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar associado com ID inexistente")
    void buscarPorIdInexistente() {

        Long id = 99L;
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            associadoService.buscarPorId(id);
        });
        
        assertEquals("Associado não encontrado com ID: " + id, exception.getMessage());
        verify(associadoRepository).findById(id);
        verifyNoInteractions(associadoMapper);
        verifyNoMoreInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve listar todos os associados com paginação e sort")
    void listarTodosComPaginacaoESort() {

        int page = 0;
        int size = 10;
        String sort = "nome";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        
        List<Associado> associados = Arrays.asList(associado);
        Page<Associado> pageAssociados = new PageImpl<>(associados, pageable, associados.size());
        
        when(associadoRepository.findAll(pageable)).thenReturn(pageAssociados);
        when(associadoMapper.toListarResponse(associado)).thenReturn(associadoListarResponse);
        
        Page<AssociadoListarResponse> resultado = associadoService.listarTodos(page, size, sort);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        
        verify(associadoRepository).findAll(pageable);
        verify(associadoMapper).toListarResponse(associado);
        verifyNoMoreInteractions(associadoRepository, associadoMapper);
    }
    
    @Test
    @DisplayName("Deve listar todos os associados sem informar sort (usando default)")
    void listarTodosComPaginacaoSemSort() {

        int page = 0;
        int size = 10;
        String sort = null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome"));
        
        List<Associado> associados = Arrays.asList(associado);
        Page<Associado> pageAssociados = new PageImpl<>(associados, pageable, associados.size());
        
        when(associadoRepository.findAll(pageable)).thenReturn(pageAssociados);
        when(associadoMapper.toListarResponse(associado)).thenReturn(associadoListarResponse);
        
        Page<AssociadoListarResponse> resultado = associadoService.listarTodos(page, size, sort);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        
        verify(associadoRepository).findAll(pageable);
        verify(associadoMapper).toListarResponse(associado);
        verifyNoMoreInteractions(associadoRepository, associadoMapper);
    }
    
    @Test
    @DisplayName("Deve atualizar um associado com sucesso")
    void atualizarAssociadoSucesso() {

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
        
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoMapper.toResponse(associado)).thenReturn(responseAtualizado);
        
        AssociadoResponse resultado = associadoService.atualizar(id, requestAtualizado);
        
        assertNotNull(resultado);
        assertEquals(requestAtualizado.getNome(), resultado.getNome());
        assertEquals(requestAtualizado.getCpf(), resultado.getCpf());
        assertEquals(requestAtualizado.getEmail(), resultado.getEmail());
        
        verify(associadoRepository).findById(id);
        verify(associadoMapper).toResponse(associado);
        verifyNoMoreInteractions(associadoRepository, associadoMapper);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar associado com ID inexistente")
    void atualizarAssociadoInexistente() {

        Long id = 99L;
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            associadoService.atualizar(id, associadoRequest);
        });
        
        assertEquals("Associado não encontrado", exception.getMessage());
        verify(associadoRepository).findById(id);
        verifyNoInteractions(associadoMapper);
        verifyNoMoreInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve deletar um associado com sucesso")
    void deletarAssociadoSucesso() {
        Long id = 1L;
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(pautaRepository.existsByCriadorId(id)).thenReturn(false);
        doNothing().when(associadoRepository).delete(associado);
    
        associadoService.deletar(id);
    
        verify(associadoRepository).findById(id);
        verify(pautaRepository).existsByCriadorId(id);
        verify(associadoRepository).delete(associado);
        verifyNoMoreInteractions(associadoRepository, pautaRepository);
        verifyNoInteractions(associadoMapper);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar associado com ID inexistente")
    void deletarAssociadoInexistente() {

        Long id = 99L;
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            associadoService.deletar(id);
        });
        
        assertEquals("Associado não encontrado", exception.getMessage());
        verify(associadoRepository).findById(id);
        verifyNoMoreInteractions(associadoRepository);
        verifyNoInteractions(associadoMapper);
    }
}