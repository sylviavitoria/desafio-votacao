package com.sylviavitoria.api_votacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.api_votacao.dto.VotacaoResumoDTO;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.model.Voto;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    boolean existsByAssociadoIdAndPautaId(Long associadoId, Long pautaId);
    
    Optional<Voto> findByAssociadoIdAndPautaId(Long associadoId, Long pautaId);
    
    List<Voto> findByPautaId(Long pautaId);
    
    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.opcao = :opcao")
    long countByPautaIdAndOpcao(Long pautaId, OpcaoVoto opcao);

    @Query("SELECT new com.sylviavitoria.api_votacao.dto.VotacaoResumoDTO(" +
           "COUNT(CASE WHEN v.opcao = 'SIM' THEN 1 END), " +
           "COUNT(CASE WHEN v.opcao = 'NAO' THEN 1 END)) " +
           "FROM Voto v WHERE v.pauta.id = :pautaId")
    VotacaoResumoDTO contabilizarVotosPorPauta(Long pautaId);
}