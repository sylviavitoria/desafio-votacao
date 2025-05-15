CREATE TABLE tb_pautas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    criador_id BIGINT NOT NULL,
    CONSTRAINT fk_pauta_criador FOREIGN KEY (criador_id) REFERENCES tb_associados (id)
);