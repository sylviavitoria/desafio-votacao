CREATE TABLE tb_sessoes_votacao (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_sessao_pauta FOREIGN KEY (pauta_id) REFERENCES tb_pautas (id),
    CONSTRAINT uk_pauta_id UNIQUE (pauta_id)
);