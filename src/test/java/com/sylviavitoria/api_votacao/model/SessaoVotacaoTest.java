package com.sylviavitoria.api_votacao.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.enums.StatusSessao;

class SessaoVotacaoTest {

    private SessaoVotacao sessao;
    private Pauta pauta;
    private LocalDateTime agora;

    @BeforeEach
    void setUp() {
        agora = LocalDateTime.now();

        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Pauta Teste");
        pauta.setStatus(StatusPauta.CRIADA);

        sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPauta(pauta);
        sessao.setStatus(StatusSessao.FECHADA);
    }

    @Test
    @DisplayName("Deve retornar true quando sessão estiver no período de votação")
    void estaAbertaDentroHorario() {

        sessao.setDataAbertura(agora.minusMinutes(30));
        sessao.setDataFechamento(agora.plusMinutes(30));

        assertTrue(sessao.estaAberta());
    }

    @Test
    @DisplayName("Deve retornar true quando exatamente no horário de abertura")
    void estaAbertaNoHorarioAbertura() {

        sessao.setDataAbertura(agora);
        sessao.setDataFechamento(agora.plusMinutes(30));

        assertTrue(sessao.estaAberta());
    }

    @Test
    @DisplayName("Deve retornar true quando exatamente no horário de fechamento")
    void estaAbertaNoHorarioFechamento() {

        LocalDateTime horaAgora = LocalDateTime.now();
        LocalDateTime horaFechamento = horaAgora.withNano(0);
        LocalDateTime horaAbertura = horaFechamento.minusMinutes(30);

        sessao.setDataAbertura(horaAbertura);
        sessao.setDataFechamento(horaAgora); 

        assertTrue(sessao.estaAberta(), "Sessão deve estar aberta no horário exato de fechamento");
    }

    @Test
    @DisplayName("Deve retornar false quando antes do horário de abertura")
    void estaAbertaAntesDaAbertura() {

        sessao.setDataAbertura(agora.plusMinutes(30));
        sessao.setDataFechamento(agora.plusMinutes(60));

        assertFalse(sessao.estaAberta());
    }

    @Test
    @DisplayName("Deve retornar false quando após o horário de fechamento")
    void estaAbertaAposOFechamento() {

        sessao.setDataAbertura(agora.minusMinutes(60));
        sessao.setDataFechamento(agora.minusMinutes(30));

        assertFalse(sessao.estaAberta());
    }

    @Test
    @DisplayName("Deve retornar true quando deve ser finalizada")
    void deveSerFinalizadaQuandoAposHorarioEAberta() {

        sessao.setStatus(StatusSessao.ABERTA);
        sessao.setDataAbertura(agora.minusMinutes(60));
        sessao.setDataFechamento(agora.minusMinutes(30));

        assertTrue(sessao.deveSerFinalizada());
    }

    @Test
    @DisplayName("Deve retornar false quando não deve ser finalizada por estar fechada")
    void naoDeveSerFinalizadaQuandoFechada() {

        sessao.setStatus(StatusSessao.FECHADA);
        sessao.setDataAbertura(agora.minusMinutes(60));
        sessao.setDataFechamento(agora.minusMinutes(30));

        assertFalse(sessao.deveSerFinalizada());
    }

    @Test
    @DisplayName("Deve retornar false quando não deve ser finalizada por estar dentro do horário")
    void naoDeveSerFinalizadaQuandoDentroHorario() {

        sessao.setStatus(StatusSessao.ABERTA);
        sessao.setDataAbertura(agora.minusMinutes(30));
        sessao.setDataFechamento(agora.plusMinutes(30));

        assertFalse(sessao.deveSerFinalizada());
    }

    @Test
    @DisplayName("Deve finalizar sessão e atualizar status da pauta")
    void finalizarSessaoComPauta() {

        sessao.setStatus(StatusSessao.ABERTA);
        pauta.setStatus(StatusPauta.EM_VOTACAO);

        sessao.finalizar();

        assertEquals(StatusSessao.FINALIZADA, sessao.getStatus());
        assertEquals(StatusPauta.EMPATADA, sessao.getPauta().getStatus());
    }

    @Test
    @DisplayName("Deve finalizar sessão sem pauta")
    void finalizarSessaoSemPauta() {

        sessao.setStatus(StatusSessao.ABERTA);
        sessao.setPauta(null);

        sessao.finalizar();

        assertEquals(StatusSessao.FINALIZADA, sessao.getStatus());
    }
}