package com.sylviavitoria.api_votacao.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import com.sylviavitoria.api_votacao.enums.StatusPauta;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_pautas")
public class Pauta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @CreationTimestamp
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPauta status = StatusPauta.CRIADA;
    
    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Associado criador;
}