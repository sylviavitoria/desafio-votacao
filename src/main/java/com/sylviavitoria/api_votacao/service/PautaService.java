package com.sylviavitoria.api_votacao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;
import com.sylviavitoria.api_votacao.interfaces.IPauta;
import com.sylviavitoria.api_votacao.mapper.PautaMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import com.sylviavitoria.api_votacao.repository.PautaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService implements IPauta {

    private final PautaRepository pautaRepository;
    private final AssociadoRepository associadoRepository;
    private final PautaMapper pautaMapper;

    @Override
    @Transactional
    public PautaResponse criar(PautaRequest request) {
        log.info("Iniciando criação de pauta: {}, criador: {}", request.getTitulo(), request.getCriadorId());

        Associado criador = associadoRepository.findById(request.getCriadorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Associado não encontrado com ID: " + request.getCriadorId()));

        Pauta pauta = pautaMapper.toEntity(request);
        pauta.setCriador(criador);
        pauta.setStatus(StatusPauta.CRIADA);

        Pauta pautaSalva = pautaRepository.save(pauta);
        log.info("Pauta criada com sucesso: ID {}", pautaSalva.getId());

        return pautaMapper.toResponse(pautaSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public PautaResponse buscarPorId(Long id) {
        log.info("Buscando pauta por ID: {}", id);
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada com ID: " + id));

        return pautaMapper.toResponse(pauta);
    }
}