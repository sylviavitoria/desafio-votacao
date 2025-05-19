package com.sylviavitoria.api_votacao.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.sylviavitoria.api_votacao.enums.OpcaoVoto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_votos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"associado_id", "pauta_id"})
})
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;
    
    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;
    
    @Enumerated(EnumType.STRING)
    private OpcaoVoto opcao;
    
    @CreationTimestamp
    private LocalDateTime dataHora;
}