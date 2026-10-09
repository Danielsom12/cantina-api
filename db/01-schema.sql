-- ESQUEMA PROVISORIO para desenvolvimento local.
-- A professora vai fornecer o banco oficial do tema; quando chegar, substitua estes scripts
-- e ajuste os nomes de tabela/coluna nos DAOs (pacote br.com.cantina.dao), se necessario.

CREATE TABLE cliente (
    id    BIGSERIAL    PRIMARY KEY,
    nome  VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE produto (
    id      BIGSERIAL     PRIMARY KEY,
    nome    VARCHAR(120)  NOT NULL,
    preco   NUMERIC(10,2) NOT NULL CHECK (preco > 0),
    estoque INTEGER       NOT NULL CHECK (estoque >= 0)   -- estoque nunca negativo (defesa no banco)
);

CREATE TABLE pedido (
    id               BIGSERIAL     PRIMARY KEY,
    cliente_id       BIGINT        NOT NULL REFERENCES cliente (id) ON DELETE RESTRICT,
    produto_id       BIGINT        NOT NULL REFERENCES produto (id) ON DELETE RESTRICT,
    preco_registrado NUMERIC(10,2) NOT NULL,              -- copia do preco no momento do pedido
    data_pedido      TIMESTAMP     NOT NULL DEFAULT now(),
    situacao         VARCHAR(20)   NOT NULL DEFAULT 'ATIVO' CHECK (situacao IN ('ATIVO', 'CANCELADO'))
);

CREATE INDEX idx_pedido_produto ON pedido (produto_id);
