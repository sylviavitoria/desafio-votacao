package com.sylviavitoria.api_votacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.api_votacao.model.Pauta;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    boolean existsByCriadorId(Long criadorId);
}