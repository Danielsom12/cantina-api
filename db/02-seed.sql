-- Dados iniciais PROVISORIOS (o banco oficial trara os seus).
INSERT INTO cliente (nome, email) VALUES
    ('Ana Souza',      'ana.souza@email.com'),
    ('Bruno Lima',     'bruno.lima@email.com'),
    ('Carla Mendes',   'carla.mendes@email.com');

INSERT INTO produto (nome, preco, estoque) VALUES
    ('Coxinha',           7.50, 20),
    ('Pao de queijo',     5.00, 30),
    ('Suco de laranja',   8.00, 10),
    ('Cafe expresso',     4.50, 50),
    ('Bolo de cenoura',   6.00,  0);   -- sem estoque: cenario "pedir produto sem estoque"
