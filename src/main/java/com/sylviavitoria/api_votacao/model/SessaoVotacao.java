package com.sylviavitoria.api_votacao.model;

import java.time.LocalDateTime;

import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.enums.StatusSessao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_sessoes_votacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    private StatusSessao status = StatusSessao.FECHADA;

    public boolean estaAberta() {
        LocalDateTime agora = LocalDateTime.now();
        return (agora.isAfter(this.dataAbertura) || agora.isEqual(this.dataAbertura)) &&
                (agora.isBefore(this.dataFechamento) || agora.isEqual(this.dataFechamento));
    }

    public boolean deveSerFinalizada() {
        return this.status == StatusSessao.ABERTA &&
                LocalDateTime.now().isAfter(this.dataFechamento);
    }

    public void finalizar() {
        this.status = StatusSessao.FINALIZADA;
        if (this.pauta != null) {
            this.pauta.setStatus(StatusPauta.EMPATADA);
        }
    }
}
