package com.sylviavitoria.api_votacao.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.api_votacao.dto.SessaoVotacaoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoRequest;
import com.sylviavitoria.api_votacao.dto.SessaoVotacaoResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.enums.StatusSessao;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.interfaces.ISessaoVotacao;
import com.sylviavitoria.api_votacao.mapper.SessaoVotacaoMapper;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;
import com.sylviavitoria.api_votacao.repository.PautaRepository;
import com.sylviavitoria.api_votacao.repository.SessaoVotacaoRepository;
import com.sylviavitoria.api_votacao.repository.VotoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoVotacaoService implements ISessaoVotacao {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoMapper sessaoVotacaoMapper;
    private final VotoRepository votoRepository;

    @Override
    @Transactional
    public SessaoVotacaoResponse criar(SessaoVotacaoRequest request) {
        log.info("Criando sessão de votação para pauta ID: {}", request.getPautaId());

        Pauta pauta = pautaRepository.findById(request.getPautaId())
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

        if (sessaoVotacaoRepository.existsByPautaId(request.getPautaId())) {
            throw new BusinessException("Já existe uma sessão de votação para esta pauta");
        }

        validarDatasAgendamento(request);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        LocalDateTime agora = LocalDateTime.now();

        if (request.getDataInicio() != null && request.getDataFim() != null) {
            if (request.getDataInicio().isAfter(request.getDataFim())) {
                throw new BusinessException("Data de início deve ser anterior à data de término");
            }
            sessao.setDataAbertura(request.getDataInicio());
            sessao.setDataFechamento(request.getDataFim());
            configurarStatusSessao(sessao, agora);
        } else {
            int duracao = (request.getDuracaoMinutos() != null) ? request.getDuracaoMinutos() : 1;
            sessao.setDataAbertura(agora);
            sessao.setDataFechamento(agora.plusMinutes(duracao));
            sessao.setStatus(StatusSessao.ABERTA);
            pauta.setStatus(StatusPauta.EM_VOTACAO);
        }

        SessaoVotacao sessaoSalva = sessaoVotacaoRepository.save(sessao);
        return sessaoVotacaoMapper.toResponse(sessaoSalva);
    }

    @Transactional
    public SessaoVotacaoResponse buscarPorId(Long id) {
        SessaoVotacao sessao = sessaoVotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

        configurarStatusSessao(sessao, LocalDateTime.now());

        sessaoVotacaoRepository.save(sessao);
        if (sessao.getPauta() != null) {
            pautaRepository.save(sessao.getPauta());
        }

        return sessaoVotacaoMapper.toResponse(sessao);
    }


    @Override
    @Transactional 
    public Page<SessaoVotacaoResponse> listarTodos(int page, int size, String sort) {
        log.info("Listando sessões com paginação: página {}, tamanho {}, ordenação {}", page, size, sort);

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(sort));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("dataAbertura"));
        }

        Page<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll(pageable);
        LocalDateTime agora = LocalDateTime.now();

        sessoes.getContent().forEach(sessao -> {
            configurarStatusSessao(sessao, agora);
            sessaoVotacaoRepository.save(sessao);
            if (sessao.getPauta() != null) {
                pautaRepository.save(sessao.getPauta());
            }
        });

        return sessoes.map(sessaoVotacaoMapper::toResponse);
    }


    private void configurarStatusSessao(SessaoVotacao sessao, LocalDateTime agora) {
        if (agora.isAfter(sessao.getDataFechamento())) {
            sessao.setStatus(StatusSessao.FINALIZADA);

            long votosSim = votoRepository.countByPautaIdAndOpcao(sessao.getPauta().getId(), OpcaoVoto.SIM);
            long votosNao = votoRepository.countByPautaIdAndOpcao(sessao.getPauta().getId(), OpcaoVoto.NAO);

            if (votosSim > votosNao) {
                sessao.getPauta().setStatus(StatusPauta.APROVADA);
            } else if (votosNao > votosSim) {
                sessao.getPauta().setStatus(StatusPauta.RECUSADA);
            } else {
                sessao.getPauta().setStatus(StatusPauta.EMPATADA);
            }
        }

        else if (agora.isAfter(sessao.getDataAbertura()) || agora.isEqual(sessao.getDataAbertura())) {
            sessao.setStatus(StatusSessao.ABERTA);
            sessao.getPauta().setStatus(StatusPauta.EM_VOTACAO);
        } else {
            sessao.setStatus(StatusSessao.FECHADA);
        }

        log.info("Status da sessão atualizado: {} - Pauta: {} - Horário atual: {}",
                sessao.getStatus(), sessao.getPauta().getTitulo(), agora);
    }

    @Override
    @Transactional
    public SessaoVotacaoResponse atualizarPeriodo(Long id, SessaoVotacaoAtualizarRequest request) {
        log.info("Atualizando período da sessão ID: {}", id);

        SessaoVotacao sessao = sessaoVotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

        if (sessao.getStatus() == StatusSessao.FINALIZADA) {
            throw new BusinessException("Não é possível alterar uma sessão já finalizada");
        }

        LocalDateTime novoFechamento = null;

        if (request.getDataFim() != null) {
            if (request.getDataFim().isBefore(LocalDateTime.now())) {
                throw new BusinessException("A nova data de fechamento não pode ser no passado");
            }
            novoFechamento = request.getDataFim();
        }

        if (request.getMinutosAdicionais() != null && request.getMinutosAdicionais() > 0) {
            novoFechamento = sessao.getDataFechamento().plusMinutes(request.getMinutosAdicionais());
        }

        if (novoFechamento != null) {
            sessao.setDataFechamento(novoFechamento);
            log.info("Data de fechamento atualizada para: {}", novoFechamento);
        }

        SessaoVotacao sessaoAtualizada = sessaoVotacaoRepository.save(sessao);
        return sessaoVotacaoMapper.toResponse(sessaoAtualizada);
    }

    private void validarDatasAgendamento(SessaoVotacaoRequest request) {
        LocalDateTime agora = LocalDateTime.now();

        if (request.getDataInicio() != null || request.getDataFim() != null) {
           
            if (request.getDataInicio() == null || request.getDataFim() == null) {
                throw new BusinessException("É necessário informar tanto a data de início quanto a de fim");
            }

            if (request.getDataInicio().isBefore(agora)) {
                throw new BusinessException("A data de início não pode ser anterior à data atual");
            }

            if (request.getDataInicio().isEqual(agora)) {
                throw new BusinessException("A data de início deve ser pelo menos 1 minuto após a data atual");
            }

            if (request.getDataFim().isBefore(request.getDataInicio())) {
                throw new BusinessException("A data de fim não pode ser anterior à data de início");
            }

            if (request.getDataFim().isEqual(request.getDataInicio())) {
                throw new BusinessException("O período de votação deve ser de no mínimo 1 minuto");
            }

        }
    }
}
