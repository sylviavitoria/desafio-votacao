CREATE TABLE tb_votos (
    id BIGSERIAL PRIMARY KEY,
    associado_id BIGINT NOT NULL,
    pauta_id BIGINT NOT NULL,
    opcao VARCHAR(3) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_voto_associado FOREIGN KEY (associado_id) REFERENCES tb_associados (id),
    CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES tb_pautas (id),
    CONSTRAINT uk_associado_pauta UNIQUE (associado_id, pauta_id)
);
