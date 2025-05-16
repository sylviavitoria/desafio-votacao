package com.sylviavitoria.api_votacao.service;

import com.sylviavitoria.api_votacao.dto.AssociadoDTO;
import com.sylviavitoria.api_votacao.dto.PautaAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.mapper.PautaMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import com.sylviavitoria.api_votacao.repository.PautaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTest {
    
    @Mock
    private PautaRepository pautaRepository;
    
    @Mock
    private AssociadoRepository associadoRepository;
    
    @Mock
    private PautaMapper pautaMapper;
    
    @InjectMocks
    private PautaService pautaService;
    
    private PautaRequest pautaRequest;
    private Pauta pauta;
    private Associado associado;
    private PautaResponse pautaResponse;
    private PautaAtualizarRequest pautaAtualizarRequest;
    
    @BeforeEach
    void setUp() {

        associado = new Associado();
        associado.setId(1L);
        associado.setNome("João da Silva");
        associado.setCpf("12345678901");
        associado.setEmail("joao@exemplo.com");
        
        pautaRequest = new PautaRequest();
        pautaRequest.setTitulo("Assembleia Geral 2025");
        pautaRequest.setDescricao("Discussão sobre os resultados financeiros de 2024");
        pautaRequest.setCriadorId(1L);
        
        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Assembleia Geral 2025");
        pauta.setDescricao("Discussão sobre os resultados financeiros de 2024");
        pauta.setCriador(associado);
        pauta.setStatus(StatusPauta.CRIADA);
        pauta.setDataCriacao(LocalDateTime.now());
        
        AssociadoDTO associadoDTO = AssociadoDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .build();
        
        pautaResponse = PautaResponse.builder()
                .id(1L)
                .titulo("Assembleia Geral 2025")
                .descricao("Discussão sobre os resultados financeiros de 2024")
                .status(StatusPauta.CRIADA)
                .dataCriacao(pauta.getDataCriacao())
                .criador(associadoDTO)
                .build();
        
        pautaAtualizarRequest = new PautaAtualizarRequest();
        pautaAtualizarRequest.setTitulo("Assembleia Geral 2025 - Atualizada");
        pautaAtualizarRequest.setDescricao("Discussão atualizada sobre os resultados financeiros de 2024");
    }
    
    @Test
    @DisplayName("Deve criar uma pauta com sucesso")
    void criarPautaSucesso() {
        when(associadoRepository.findById(pautaRequest.getCriadorId())).thenReturn(Optional.of(associado));
        when(pautaMapper.toEntity(pautaRequest)).thenReturn(pauta);
        when(pautaRepository.save(pauta)).thenReturn(pauta);
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);
        
        PautaResponse resultado = pautaService.criar(pautaRequest);
        
        assertNotNull(resultado);
        assertEquals(pautaRequest.getTitulo(), resultado.getTitulo());
        assertEquals(pautaRequest.getDescricao(), resultado.getDescricao());
        assertEquals(StatusPauta.CRIADA, resultado.getStatus());
        assertEquals(associado.getId(), resultado.getCriador().getId());
        
        verify(associadoRepository).findById(pautaRequest.getCriadorId());
        verify(pautaMapper).toEntity(pautaRequest);
        verify(pautaRepository).save(pauta);
        verify(pautaMapper).toResponse(pauta);
        verifyNoMoreInteractions(pautaRepository, associadoRepository, pautaMapper);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar pauta com criador inexistente")
    void criarPautaCriadorInexistente() {
        when(associadoRepository.findById(pautaRequest.getCriadorId())).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pautaService.criar(pautaRequest);
        });
        
        assertEquals("Associado não encontrado com ID: " + pautaRequest.getCriadorId(), exception.getMessage());
        verify(associadoRepository).findById(pautaRequest.getCriadorId());
        verifyNoInteractions(pautaMapper, pautaRepository);
        verifyNoMoreInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve buscar pauta por ID com sucesso")
    void buscarPorIdSucesso() {
        Long id = 1L;
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);
        
        PautaResponse resultado = pautaService.buscarPorId(id);
        
        assertNotNull(resultado);
        assertEquals(pauta.getId(), resultado.getId());
        assertEquals(pauta.getTitulo(), resultado.getTitulo());
        assertEquals(pauta.getDescricao(), resultado.getDescricao());
        assertEquals(pauta.getStatus(), resultado.getStatus());
        
        verify(pautaRepository).findById(id);
        verify(pautaMapper).toResponse(pauta);
        verifyNoMoreInteractions(pautaRepository, pautaMapper);
        verifyNoInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar pauta com ID inexistente")
    void buscarPorIdInexistente() {
        Long id = 99L;
        when(pautaRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pautaService.buscarPorId(id);
        });
        
        assertEquals("Pauta não encontrada com ID: " + id, exception.getMessage());
        verify(pautaRepository).findById(id);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
    
    @Test
    @DisplayName("Deve listar todas as pautas com paginação e sort")
    void listarTodosComPaginacaoESort() {
        int page = 0;
        int size = 10;
        String sort = "titulo";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        
        List<Pauta> pautas = Arrays.asList(pauta);
        Page<Pauta> pagePautas = new PageImpl<>(pautas, pageable, pautas.size());
        
        when(pautaRepository.findAll(pageable)).thenReturn(pagePautas);
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);
        
        Page<PautaResponse> resultado = pautaService.listarTodos(page, size, sort);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        
        verify(pautaRepository).findAll(pageable);
        verify(pautaMapper).toResponse(pauta);
        verifyNoMoreInteractions(pautaRepository, pautaMapper);
        verifyNoInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve listar todas as pautas sem informar sort (usando default)")
    void listarTodosComPaginacaoSemSort() {
        int page = 0;
        int size = 10;
        String sort = null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo"));
        
        List<Pauta> pautas = Arrays.asList(pauta);
        Page<Pauta> pagePautas = new PageImpl<>(pautas, pageable, pautas.size());
        
        when(pautaRepository.findAll(pageable)).thenReturn(pagePautas);
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);
        
        Page<PautaResponse> resultado = pautaService.listarTodos(page, size, sort);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        
        verify(pautaRepository).findAll(pageable);
        verify(pautaMapper).toResponse(pauta);
        verifyNoMoreInteractions(pautaRepository, pautaMapper);
        verifyNoInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso")
    void atualizarPautaSucesso() {
        Long id = 1L;
        Pauta pautaAtualizada = new Pauta();
        pautaAtualizada.setId(id);
        pautaAtualizada.setTitulo(pautaAtualizarRequest.getTitulo());
        pautaAtualizada.setDescricao(pautaAtualizarRequest.getDescricao());
        pautaAtualizada.setStatus(StatusPauta.CRIADA);
        pautaAtualizada.setCriador(associado);
        pautaAtualizada.setDataCriacao(pauta.getDataCriacao());
        
        PautaResponse responseAtualizado = PautaResponse.builder()
                .id(id)
                .titulo(pautaAtualizarRequest.getTitulo())
                .descricao(pautaAtualizarRequest.getDescricao())
                .status(StatusPauta.CRIADA)
                .dataCriacao(pauta.getDataCriacao())
                .criador(pautaResponse.getCriador())
                .build();
        
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(pauta)).thenReturn(pautaAtualizada);
        when(pautaMapper.toResponse(pautaAtualizada)).thenReturn(responseAtualizado);
        
        PautaResponse resultado = pautaService.atualizar(id, pautaAtualizarRequest);
        
        assertNotNull(resultado);
        assertEquals(pautaAtualizarRequest.getTitulo(), resultado.getTitulo());
        assertEquals(pautaAtualizarRequest.getDescricao(), resultado.getDescricao());
        assertEquals(StatusPauta.CRIADA, resultado.getStatus());
        
        verify(pautaRepository).findById(id);
        verify(pautaRepository).save(pauta);
        verify(pautaMapper).toResponse(pautaAtualizada);
        verifyNoMoreInteractions(pautaRepository, pautaMapper);
        verifyNoInteractions(associadoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar pauta com ID inexistente")
    void atualizarPautaInexistente() {
        Long id = 99L;
        when(pautaRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pautaService.atualizar(id, pautaAtualizarRequest);
        });
        
        assertEquals("Pauta não encontrada com ID: " + id, exception.getMessage());
        verify(pautaRepository).findById(id);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao atualizar pauta que não está no status CRIADA")
    void atualizarPautaNaoEditavel() {
        Long id = 1L;
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            pautaService.atualizar(id, pautaAtualizarRequest);
        });
        
        assertEquals("Não é possível editar uma pauta que já está em votação ou encerrada", exception.getMessage());
        verify(pautaRepository).findById(id);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
    
    @Test
    @DisplayName("Deve deletar uma pauta com sucesso")
    void deletarPautaSucesso() {
        Long id = 1L;
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));
        doNothing().when(pautaRepository).delete(pauta);
        
        pautaService.deletar(id);
        
        verify(pautaRepository).findById(id);
        verify(pautaRepository).delete(pauta);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar pauta com ID inexistente")
    void deletarPautaInexistente() {
        Long id = 99L;
        when(pautaRepository.findById(id)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pautaService.deletar(id);
        });
        
        assertEquals("Pauta não encontrada com ID: " + id, exception.getMessage());
        verify(pautaRepository).findById(id);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar pauta que está em votação")
    void deletarPautaEmVotacao() {
        Long id = 1L;
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pautaService.deletar(id);
        });
        
        assertEquals("Não é possível deletar uma pauta que está em votação", exception.getMessage());
        verify(pautaRepository).findById(id);
        verifyNoMoreInteractions(pautaRepository);
        verifyNoInteractions(pautaMapper, associadoRepository);
    }
}