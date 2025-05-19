package com.sylviavitoria.api_votacao.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sylviavitoria.api_votacao.dto.AssociadoDTO;
import com.sylviavitoria.api_votacao.dto.PautaRequest;
import com.sylviavitoria.api_votacao.dto.PautaResponse;
import com.sylviavitoria.api_votacao.enums.OpcaoVoto;
import com.sylviavitoria.api_votacao.enums.StatusPauta;
import com.sylviavitoria.api_votacao.model.Associado;
import com.sylviavitoria.api_votacao.model.Pauta;
import com.sylviavitoria.api_votacao.repository.VotoRepository;

@ExtendWith(MockitoExtension.class)
class PautaMapperTest {

    @Mock
    private VotoRepository votoRepository;

    @Spy
    @InjectMocks
    private PautaMapperImpl mapper;

    private PautaRequest pautaRequest;
    private Pauta pauta;
    private Associado associado;

    @BeforeEach
    void setUp() {

        pautaRequest = new PautaRequest();
        pautaRequest.setTitulo("Título da Pauta");
        pautaRequest.setDescricao("Descrição da Pauta");
        pautaRequest.setCriadorId(1L);

        associado = new Associado();
        associado.setId(1L);
        associado.setNome("João da Silva");

        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Título da Pauta");
        pauta.setDescricao("Descrição da Pauta");
        pauta.setStatus(StatusPauta.CRIADA);
        pauta.setDataCriacao(LocalDateTime.now());
        pauta.setCriador(associado);
    }

    @Test
    @DisplayName("Deve converter PautaRequest para Entity")
    void toEntityTest() {

        Pauta result = mapper.toEntity(pautaRequest);

        assertNotNull(result);
        assertEquals(pautaRequest.getTitulo(), result.getTitulo());
        assertEquals(pautaRequest.getDescricao(), result.getDescricao());
        assertNull(result.getId());
        assertNull(result.getDataCriacao());
        assertEquals(StatusPauta.CRIADA, result.getStatus());
        assertNull(result.getCriador());

        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve converter Entity para PautaResponse")
    void toResponseTest() {

        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO)).thenReturn(2L);

        PautaResponse result = mapper.toResponse(pauta);

        assertNotNull(result);
        assertEquals(pauta.getId(), result.getId());
        assertEquals(pauta.getTitulo(), result.getTitulo());
        assertEquals(pauta.getDescricao(), result.getDescricao());
        assertEquals(pauta.getStatus(), result.getStatus());
        assertEquals(pauta.getDataCriacao(), result.getDataCriacao());
        assertEquals(3L, result.getTotalVotosSim());
        assertEquals(2L, result.getTotalVotosNao());

        AssociadoDTO criadorDTO = result.getCriador();
        assertNotNull(criadorDTO);
        assertEquals(associado.getId(), criadorDTO.getId());
        assertEquals(associado.getNome(), criadorDTO.getNome());

        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.SIM);
        verify(votoRepository).countByPautaIdAndOpcao(pauta.getId(), OpcaoVoto.NAO);
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve retornar null ao converter Associado null para DTO")
    void toAssociadoDTONullTest() {

        AssociadoDTO result = mapper.toAssociadoDTO(null);

        assertNull(result);

        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve converter Associado para DTO")
    void toAssociadoDTOTest() {

        AssociadoDTO result = mapper.toAssociadoDTO(associado);

        assertNotNull(result);
        assertEquals(associado.getId(), result.getId());
        assertEquals(associado.getNome(), result.getNome());

        verifyNoInteractions(votoRepository);
    }
}