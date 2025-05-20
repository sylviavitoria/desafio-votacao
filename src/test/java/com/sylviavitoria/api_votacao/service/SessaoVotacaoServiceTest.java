package com.sylviavitoria.api_votacao.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.enums.StatusSessao;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.mapper.SessaoVotacaoMapper;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;
import com.sylviavitoria.api_votacao.repository.PautaRepository;
import com.sylviavitoria.api_votacao.repository.SessaoVotacaoRepository;
import com.sylviavitoria.api_votacao.repository.VotoRepository;

@ExtendWith(MockitoExtension.class)
public class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoMapper sessaoVotacaoMapper;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    private SessaoVotacaoRequest sessaoRequest;
    private SessaoVotacao sessao;
    private SessaoVotacaoResponse sessaoResponse;
    private Pauta pauta;
    private LocalDateTime agora;

    @BeforeEach
    void setUp() {
        agora = LocalDateTime.now();

        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Pauta Teste");
        pauta.setStatus(StatusPauta.CRIADA);

        sessaoRequest = new SessaoVotacaoRequest();
        sessaoRequest.setPautaId(1L);
        sessaoRequest.setDataInicio(agora.plusMinutes(5));
        sessaoRequest.setDataFim(agora.plusMinutes(10));

        sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPauta(pauta);
        sessao.setDataAbertura(agora.plusMinutes(5));
        sessao.setDataFechamento(agora.plusMinutes(10));
        sessao.setStatus(StatusSessao.FECHADA);

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
    @DisplayName("Deve criar uma sessão com sucesso usando datas específicas")
    void criarSessaoComDatasEspecificas() {

        when(pautaRepository.findById(sessaoRequest.getPautaId())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId())).thenReturn(false);
        when(sessaoVotacaoRepository.save(argThat(sessaoParaSalvar -> sessaoParaSalvar.getPauta().equals(pauta) &&
                sessaoParaSalvar.getDataAbertura().equals(sessaoRequest.getDataInicio()) &&
                sessaoParaSalvar.getDataFechamento().equals(sessaoRequest.getDataFim()) &&
                sessaoParaSalvar.getStatus() == StatusSessao.FECHADA))).thenReturn(sessao);
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse resultado = sessaoVotacaoService.criar(sessaoRequest);

        assertNotNull(resultado);
        assertEquals(sessaoResponse, resultado);

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).save(argThat(s -> s.getPauta().equals(pauta)));
        verify(sessaoVotacaoMapper).toResponse(sessao);
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository, sessaoVotacaoMapper);
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve criar uma sessão com sucesso usando duração padrão")
    void criarSessaoComDuracaoPadrao() {

        sessaoRequest.setDataInicio(null);
        sessaoRequest.setDataFim(null);

        when(pautaRepository.findById(sessaoRequest.getPautaId())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId())).thenReturn(false);
        when(sessaoVotacaoRepository.save(argThat(sessao -> sessao.getPauta().equals(pauta) &&
                sessao.getStatus() == StatusSessao.ABERTA))).thenReturn(sessao);
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse resultado = sessaoVotacaoService.criar(sessaoRequest);

        assertNotNull(resultado);
        assertEquals(sessaoResponse, resultado);

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).save(argThat(s -> s.getStatus() == StatusSessao.ABERTA));
        verify(sessaoVotacaoMapper).toResponse(sessao);
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository, sessaoVotacaoMapper);
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve buscar sessão por ID com sucesso")
    void buscarPorIdSucesso() {

        Long id = 1L;
        when(sessaoVotacaoRepository.findById(id)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse resultado = sessaoVotacaoService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(sessaoResponse, resultado);

        verify(sessaoVotacaoRepository).findById(id);
        verify(sessaoVotacaoRepository).save(sessao);
        verify(sessaoVotacaoMapper).toResponse(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoMoreInteractions(sessaoVotacaoRepository, sessaoVotacaoMapper, pautaRepository);
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve atualizar período com sucesso usando nova data fim")
    void atualizarPeriodoComNovaDataFimSucesso() {

        Long id = 1L;
        LocalDateTime novaDataFim = agora.plusHours(1);
        SessaoVotacaoAtualizarRequest request = new SessaoVotacaoAtualizarRequest();
        request.setDataFim(novaDataFim);

        when(sessaoVotacaoRepository.findById(id)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoRepository.save(argThat(s -> s.getDataFechamento().equals(novaDataFim)))).thenReturn(sessao);
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse resultado = sessaoVotacaoService.atualizarPeriodo(id, request);

        assertNotNull(resultado);
        assertEquals(sessaoResponse, resultado);

        verify(sessaoVotacaoRepository).findById(id);
        verify(sessaoVotacaoRepository).save(argThat(s -> s.getDataFechamento().equals(novaDataFim)));
        verify(sessaoVotacaoMapper).toResponse(sessao);
        verifyNoMoreInteractions(sessaoVotacaoRepository, sessaoVotacaoMapper);
        verifyNoInteractions(pautaRepository, votoRepository);
    }

    @Test
    @DisplayName("Deve listar todas as sessões com paginação e ordenação")
    void listarTodosComPaginacaoESort() {

        int page = 0;
        int size = 10;
        String sort = "dataAbertura";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SessaoVotacao> pageSessoes = new PageImpl<>(Arrays.asList(sessao));

        when(sessaoVotacaoRepository.findAll(pageable)).thenReturn(pageSessoes);
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        Page<SessaoVotacaoResponse> resultado = sessaoVotacaoService.listarTodos(page, size, sort);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(sessaoResponse, resultado.getContent().get(0));

        verify(sessaoVotacaoRepository).findAll(pageable);
        verify(sessaoVotacaoRepository).save(sessao);
        verify(sessaoVotacaoMapper).toResponse(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoMoreInteractions(sessaoVotacaoRepository, sessaoVotacaoMapper, pautaRepository);
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve configurar status como FINALIZADA quando a sessão estiver após o fechamento")
    void configurarStatusSessaoFinalizada() {

        LocalDateTime agora = LocalDateTime.now();
        sessao.setDataAbertura(agora.minusHours(2));
        sessao.setDataFechamento(agora.minusHours(1));
        sessao.setStatus(StatusSessao.ABERTA);
        pauta.setStatus(StatusPauta.EM_VOTACAO);

        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO)).thenReturn(1L);
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse response = sessaoVotacaoService.buscarPorId(1L);

        assertEquals(StatusSessao.FINALIZADA, sessao.getStatus());
        assertEquals(StatusPauta.APROVADA, sessao.getPauta().getStatus());

        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM);
        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO);
        verify(sessaoVotacaoRepository).save(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve configurar status como RECUSADA quando tiver mais votos NÃO")
    void configurarStatusSessaoRecusada() {

        LocalDateTime agora = LocalDateTime.now();
        sessao.setDataAbertura(agora.minusHours(2));
        sessao.setDataFechamento(agora.minusHours(1));
        sessao.setStatus(StatusSessao.ABERTA);
        pauta.setStatus(StatusPauta.EM_VOTACAO);

        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM)).thenReturn(1L);
        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO)).thenReturn(3L);
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse response = sessaoVotacaoService.buscarPorId(1L);

        assertEquals(StatusSessao.FINALIZADA, sessao.getStatus());
        assertEquals(StatusPauta.RECUSADA, sessao.getPauta().getStatus());

        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM);
        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO);
        verify(sessaoVotacaoRepository).save(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve configurar status como EMPATADA quando tiver mesma quantidade de votos")
    void configurarStatusSessaoEmpatada() {

        LocalDateTime agora = LocalDateTime.now();
        sessao.setDataAbertura(agora.minusHours(2));
        sessao.setDataFechamento(agora.minusHours(1));
        sessao.setStatus(StatusSessao.ABERTA);
        pauta.setStatus(StatusPauta.EM_VOTACAO);

        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO)).thenReturn(2L);
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse response = sessaoVotacaoService.buscarPorId(1L);

        assertEquals(StatusSessao.FINALIZADA, sessao.getStatus());
        assertEquals(StatusPauta.EMPATADA, sessao.getPauta().getStatus());

        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM);
        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO);
        verify(sessaoVotacaoRepository).save(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve configurar status como ABERTA quando estiver no período de votação")
    void configurarStatusSessaoAberta() {

        LocalDateTime agora = LocalDateTime.now();
        sessao.setDataAbertura(agora.minusMinutes(30));
        sessao.setDataFechamento(agora.plusMinutes(30));
        sessao.setStatus(StatusSessao.FECHADA);
        pauta.setStatus(StatusPauta.CRIADA);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse response = sessaoVotacaoService.buscarPorId(1L);

        assertEquals(StatusSessao.ABERTA, sessao.getStatus());
        assertEquals(StatusPauta.EM_VOTACAO, sessao.getPauta().getStatus());

        verify(sessaoVotacaoRepository).save(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve manter status como FECHADA quando ainda não iniciou")
    void configurarStatusSessaoFechada() {

        LocalDateTime agora = LocalDateTime.now();
        sessao.setDataAbertura(agora.plusMinutes(30));
        sessao.setDataFechamento(agora.plusMinutes(60));
        sessao.setStatus(StatusSessao.FECHADA);
        pauta.setStatus(StatusPauta.CRIADA);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(sessaoVotacaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        SessaoVotacaoResponse response = sessaoVotacaoService.buscarPorId(1L);

        assertEquals(StatusSessao.FECHADA, sessao.getStatus());
        assertEquals(StatusPauta.CRIADA, sessao.getPauta().getStatus());

        verify(sessaoVotacaoRepository).save(sessao);
        verify(pautaRepository).save(sessao.getPauta());
        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando informar apenas data início")
    void criarSessaoApenasDataInicio() {

        sessaoRequest.setDataInicio(agora.plusMinutes(5));
        sessaoRequest.setDataFim(null);

        when(pautaRepository.findById(sessaoRequest.getPautaId()))
                .thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sessaoVotacaoService.criar(sessaoRequest);
        });

        assertEquals("É necessário informar tanto a data de início quanto a de fim",
                exception.getMessage());

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository);
        verifyNoInteractions(sessaoVotacaoMapper, votoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data início for anterior à atual")
    void criarSessaoDataInicioAnterior() {

        sessaoRequest.setDataInicio(agora.minusMinutes(5));
        sessaoRequest.setDataFim(agora.plusMinutes(5));

        when(pautaRepository.findById(sessaoRequest.getPautaId()))
                .thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sessaoVotacaoService.criar(sessaoRequest);
        });

        assertEquals("A data de início não pode ser anterior à data atual",
                exception.getMessage());

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository);
        verifyNoInteractions(sessaoVotacaoMapper, votoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data início for igual à atual")
    void criarSessaoDataInicioIgualAtual() {

        sessaoRequest.setDataInicio(agora);
        sessaoRequest.setDataFim(agora.plusMinutes(5));

        when(pautaRepository.findById(sessaoRequest.getPautaId()))
                .thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sessaoVotacaoService.criar(sessaoRequest);
        });

        assertEquals("A data de início não pode ser anterior à data atual",
                exception.getMessage());

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository);
        verifyNoInteractions(sessaoVotacaoMapper, votoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data fim for anterior à data início")
    void criarSessaoDataFimAnterior() {

        sessaoRequest.setDataInicio(agora.plusMinutes(10));
        sessaoRequest.setDataFim(agora.plusMinutes(5));

        when(pautaRepository.findById(sessaoRequest.getPautaId()))
                .thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sessaoVotacaoService.criar(sessaoRequest);
        });

        assertEquals("A data de fim não pode ser anterior à data de início",
                exception.getMessage());

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository);
        verifyNoInteractions(sessaoVotacaoMapper, votoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data fim for igual à data início")
    void criarSessaoDataFimIgualInicio() {

        LocalDateTime dataInicio = agora.plusMinutes(5);
        sessaoRequest.setDataInicio(dataInicio);
        sessaoRequest.setDataFim(dataInicio);

        when(pautaRepository.findById(sessaoRequest.getPautaId()))
                .thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaId(sessaoRequest.getPautaId()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sessaoVotacaoService.criar(sessaoRequest);
        });

        assertEquals("O período de votação deve ser de no mínimo 1 minuto",
                exception.getMessage());

        verify(pautaRepository).findById(sessaoRequest.getPautaId());
        verify(sessaoVotacaoRepository).existsByPautaId(sessaoRequest.getPautaId());
        verifyNoMoreInteractions(pautaRepository, sessaoVotacaoRepository);
        verifyNoInteractions(sessaoVotacaoMapper, votoRepository);
    }
}