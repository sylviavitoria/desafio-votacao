CREATE TABLE tb_associados (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uk_associado_cpf UNIQUE (cpf)
);