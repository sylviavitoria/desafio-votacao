package com.sylviavitoria.api_votacao.service;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.interfaces.IAssociado;
import com.sylviavitoria.api_votacao.mapper.AssociadoMapper;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.repository.AssociadoRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.api_votacao.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssociadoService implements IAssociado {

    private final AssociadoRepository associadoRepository;
    private final AssociadoMapper associadoMapper;

    @Override
    @Transactional
    public AssociadoResponse criar(AssociadoRequest request) {
        log.info("Iniciando criação de associado: {}, {}, {}", request.getNome(), request.getEmail(), request.getCpf());
        if (associadoRepository.findByCpf(request.getCpf()).isPresent()) {
            throw new EntityExistsException("Já existe um associado com este CPF");
        }

        Associado associado = associadoMapper.toEntity(request);
        Associado associadoSalvo = associadoRepository.save(associado);

        return associadoMapper.toResponse(associadoSalvo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AssociadoResponse buscarPorId(Long id) {
        log.info("Buscando associado por ID: {}", id);
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Associado não encontrado com ID: " + id));

        return associadoMapper.toResponse(associado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssociadoListarResponse> listarTodos(Pageable pageable) {
        log.info("Listando associados com paginação");
        return associadoRepository.findAll(pageable)
                .map(associadoMapper::toListarResponse);
    }

    @Override
    @Transactional
    public AssociadoResponse atualizar(Long id, AssociadoRequest request) {
        log.info("Atualido os dados de {}, {}, {}", request.getNome(), request.getEmail(), request.getCpf());
        Associado associadoAtualizado = associadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Associado não encontrado"));

        associadoAtualizado.setNome(request.getNome());
        associadoAtualizado.setEmail(request.getEmail());
        associadoAtualizado.setCpf(request.getCpf());

        return associadoMapper.toResponse(associadoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        log.info("Removendo associado com ID: {}", id);
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Associado não encontrado"));

        associadoRepository.delete(associado);
    }
}