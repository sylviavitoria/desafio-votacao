package com.sylviavitoria.api_votacao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.api_votacao.dto.ResultadoVotacaoResponse;
import com.sylviavitoria.api_votacao.dto.VotoAtualizarRequest;
import com.sylviavitoria.api_votacao.dto.VotoRequest;
import com.sylviavitoria.api_votacao.dto.VotoResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.exception.BusinessException;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.interfaces.IVoto;
import com.sylviavitoria.api_votacao.mapper.VotoMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;
import com.sylviavitoria.api_votacao.model.Voto;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import com.sylviavitoria.api_votacao.repository.PautaRepository;
import com.sylviavitoria.api_votacao.repository.SessaoVotacaoRepository;
import com.sylviavitoria.api_votacao.repository.VotoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService implements IVoto {

        private final VotoRepository votoRepository;
        private final AssociadoRepository associadoRepository;
        private final PautaRepository pautaRepository;
        private final SessaoVotacaoRepository sessaoVotacaoRepository;
        private final VotoMapper votoMapper;

        @Override
        @Transactional
        public VotoResponse registrarVoto(VotoRequest request) {
                log.info("Registrando voto para associado ID: {} na pauta ID: {}",
                                request.getAssociadoId(), request.getPautaId());

                Associado associado = associadoRepository.findById(request.getAssociadoId())
                                .orElseThrow(() -> new EntityNotFoundException("Associado não encontrado"));

                Pauta pauta = pautaRepository.findById(request.getPautaId())
                                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

                SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(request.getPautaId())
                                .orElseThrow(() -> new BusinessException(
                                                "Não existe sessão de votação para esta pauta"));

                if (!sessao.estaAberta()) {
                        throw new BusinessException("Sessão de votação não está aberta");
                }

                if (votoRepository.existsByAssociadoIdAndPautaId(request.getAssociadoId(), request.getPautaId())) {
                        throw new BusinessException("Associado já votou nesta pauta");
                }

                Voto voto = new Voto();
                voto.setAssociado(associado);
                voto.setPauta(pauta);
                voto.setOpcao(request.getOpcao());

                Voto votoSalvo = votoRepository.save(voto);
                log.info("Voto registrado com sucesso");

                return votoMapper.toResponse(votoSalvo);
        }

        @Override
        @Transactional(readOnly = true)
        public VotoResponse buscarPorId(Long id) {
                log.info("Buscando voto por ID: {}", id);

                Voto voto = votoRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Voto não encontrado"));

                return votoMapper.toResponse(voto);
        }

        @Override
        @Transactional(readOnly = true)
        public ResultadoVotacaoResponse consultarResultado(Long pautaId) {
                log.info("Consultando resultado da votação para pauta ID: {}", pautaId);

                Pauta pauta = pautaRepository.findById(pautaId)
                                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

                long votosSim = votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVoto.SIM);
                long votosNao = votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVoto.NAO);
                long totalVotos = votosSim + votosNao;

                return ResultadoVotacaoResponse.builder()
                                .pautaId(pautaId)
                                .pautaTitulo(pauta.getTitulo())
                                .votosSim(votosSim)
                                .votosNao(votosNao)
                                .totalVotos(totalVotos)
                                .build();
        }

        @Override
        @Transactional
        public VotoResponse atualizarVoto(Long id, VotoAtualizarRequest request) {
                log.info("Atualizando voto ID: {}", id);

                Voto voto = votoRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Voto não encontrado"));

                SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(voto.getPauta().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

                if (!sessao.estaAberta()) {
                        throw new BusinessException("Não é possível alterar um voto após o encerramento da sessão");
                }

                voto.setOpcao(request.getOpcao());

                Voto votoAtualizado = votoRepository.save(voto);
                return votoMapper.toResponse(votoAtualizado);
        }
}