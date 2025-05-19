package com.sylviavitoria.api_votacao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.enums.StatusSessao;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.mapper.VotoMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;
import com.sylviavitoria.api_votacao.model.Voto;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import com.sylviavitoria.api_votacao.repository.PautaRepository;
import com.sylviavitoria.api_votacao.repository.SessaoVotacaoRepository;
import com.sylviavitoria.api_votacao.repository.VotoRepository;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private VotoMapper votoMapper;

    @InjectMocks
    private VotoService votoService;

    private VotoRequest votoRequest;
    private Associado associado;
    private Pauta pauta;
    private SessaoVotacao sessao;
    private Voto voto;
    private VotoResponse votoResponse;

    @BeforeEach
    void setUp() {
        votoRequest = new VotoRequest();
        votoRequest.setAssociadoId(1L);
        votoRequest.setPautaId(1L);
        votoRequest.setOpcao(OpcaoVoto.SIM);

        associado = new Associado();
        associado.setId(1L);
        associado.setNome("João");

        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Pauta Teste");
        pauta.setStatus(StatusPauta.EM_VOTACAO);

        sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPauta(pauta);
        sessao.setDataAbertura(LocalDateTime.now().minusMinutes(30));
        sessao.setDataFechamento(LocalDateTime.now().plusMinutes(30));
        sessao.setStatus(StatusSessao.ABERTA);

        voto = new Voto();
        voto.setId(1L);
        voto.setAssociado(associado);
        voto.setPauta(pauta);
        voto.setOpcao(OpcaoVoto.SIM);

        votoResponse = new VotoResponse();
        votoResponse.setId(1L);
        votoResponse.setAssociadoId(1L);
        votoResponse.setPautaId(1L);
        votoResponse.setOpcao(OpcaoVoto.SIM);
    }

    @Test
    @DisplayName("Deve registrar voto com sucesso")
    void registrarVotoSucesso() {

        when(associadoRepository.findById(votoRequest.getAssociadoId())).thenReturn(Optional.of(associado));
        when(pautaRepository.findById(votoRequest.getPautaId())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(votoRequest.getPautaId())).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByAssociadoIdAndPautaId(votoRequest.getAssociadoId(), votoRequest.getPautaId()))
                .thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(voto);
        when(votoMapper.toResponse(voto)).thenReturn(votoResponse);

        VotoResponse resultado = votoService.registrarVoto(votoRequest);

        assertNotNull(resultado);
        assertEquals(votoResponse, resultado);

        verify(associadoRepository).findById(votoRequest.getAssociadoId());
        verify(pautaRepository).findById(votoRequest.getPautaId());
        verify(sessaoVotacaoRepository).findByPautaId(votoRequest.getPautaId());
        verify(votoRepository).existsByAssociadoIdAndPautaId(votoRequest.getAssociadoId(), votoRequest.getPautaId());
        verify(votoRepository).save(argThat(v -> v.getAssociado().equals(associado) &&
                v.getPauta().equals(pauta) &&
                v.getOpcao().equals(votoRequest.getOpcao())));
        verify(votoMapper).toResponse(voto);
        verifyNoMoreInteractions(votoRepository, associadoRepository, pautaRepository, sessaoVotacaoRepository,
                votoMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar voto com associado inexistente")
    void registrarVotoAssociadoInexistente() {

        when(associadoRepository.findById(votoRequest.getAssociadoId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            votoService.registrarVoto(votoRequest);
        });

        assertEquals("Associado não encontrado", exception.getMessage());

        verify(associadoRepository).findById(votoRequest.getAssociadoId());
        verifyNoMoreInteractions(associadoRepository);
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository, votoRepository, votoMapper);
    }

    @Test
    @DisplayName("Deve buscar voto por ID com sucesso")
    void buscarPorIdSucesso() {

        Long id = 1L;
        when(votoRepository.findById(id)).thenReturn(Optional.of(voto));
        when(votoMapper.toResponse(voto)).thenReturn(votoResponse);

        VotoResponse resultado = votoService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(votoResponse, resultado);

        verify(votoRepository).findById(id);
        verify(votoMapper).toResponse(voto);
        verifyNoMoreInteractions(votoRepository, votoMapper);
        verifyNoInteractions(associadoRepository, pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    @DisplayName("Deve consultar resultado da votação com sucesso")
    void consultarResultadoSucesso() {

        Long pautaId = 1L;
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVoto.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVoto.NAO)).thenReturn(2L);

        ResultadoVotacaoResponse resultado = votoService.consultarResultado(pautaId);

        assertNotNull(resultado);
        assertEquals(pautaId, resultado.getPautaId());
        assertEquals(pauta.getTitulo(), resultado.getPautaTitulo());
        assertEquals(3L, resultado.getVotosSim());
        assertEquals(2L, resultado.getVotosNao());
        assertEquals(5L, resultado.getTotalVotos());

        verify(pautaRepository).findById(pautaId);
        verify(votoRepository).countByPautaIdAndOpcao(pautaId, OpcaoVoto.SIM);
        verify(votoRepository).countByPautaIdAndOpcao(pautaId, OpcaoVoto.NAO);
        verifyNoMoreInteractions(pautaRepository, votoRepository);
        verifyNoInteractions(associadoRepository, sessaoVotacaoRepository, votoMapper);
    }

    @Test
    @DisplayName("Deve atualizar voto com sucesso")
    void atualizarVotoSucesso() {

        Long id = 1L;
        VotoAtualizarRequest request = new VotoAtualizarRequest();
        request.setOpcao(OpcaoVoto.NAO);

        when(votoRepository.findById(id)).thenReturn(Optional.of(voto));
        when(sessaoVotacaoRepository.findByPautaId(voto.getPauta().getId())).thenReturn(Optional.of(sessao));
        when(votoRepository.save(voto)).thenReturn(voto);
        when(votoMapper.toResponse(voto)).thenReturn(votoResponse);

        VotoResponse resultado = votoService.atualizarVoto(id, request);

        assertNotNull(resultado);
        assertEquals(votoResponse, resultado);

        verify(votoRepository).findById(id);
        verify(sessaoVotacaoRepository).findByPautaId(voto.getPauta().getId());
        verify(votoRepository).save(argThat(v -> v.getOpcao().equals(request.getOpcao())));
        verify(votoMapper).toResponse(voto);
        verifyNoMoreInteractions(votoRepository, sessaoVotacaoRepository, votoMapper);
        verifyNoInteractions(associadoRepository, pautaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar voto com sessão encerrada")
    void atualizarVotoSessaoEncerrada() {

        Long id = 1L;
        VotoAtualizarRequest request = new VotoAtualizarRequest();
        request.setOpcao(OpcaoVoto.NAO);

        sessao.setStatus(StatusSessao.FINALIZADA);
        sessao.setDataAbertura(LocalDateTime.now().minusHours(2)); 
        sessao.setDataFechamento(LocalDateTime.now().minusHours(1)); 

        when(votoRepository.findById(id)).thenReturn(Optional.of(voto));
        when(sessaoVotacaoRepository.findByPautaId(voto.getPauta().getId())).thenReturn(Optional.of(sessao));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            votoService.atualizarVoto(id, request);
        });

        assertEquals("Não é possível alterar um voto após o encerramento da sessão", exception.getMessage());

        verify(votoRepository).findById(id);
        verify(sessaoVotacaoRepository).findByPautaId(voto.getPauta().getId());
        verifyNoMoreInteractions(votoRepository, sessaoVotacaoRepository);
        verifyNoInteractions(votoMapper, associadoRepository, pautaRepository);
    }
}