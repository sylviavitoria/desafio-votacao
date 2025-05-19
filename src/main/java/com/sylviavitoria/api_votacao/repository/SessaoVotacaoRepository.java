package com.sylviavitoria.api_votacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.api_votacao.enums.StatusSessao;
import com.sylviavitoria.api_votacao.model.SessaoVotacao;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    
    Optional<SessaoVotacao> findByPautaId(Long pautaId);
    
    boolean existsByPautaId(Long pautaId);
    
    List<SessaoVotacao> findByStatus(StatusSessao status);
}