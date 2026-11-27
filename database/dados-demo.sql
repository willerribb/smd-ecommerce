-- demo data, run it right after recreating the db (running it twice gives a duplicate name error)
-- codespace: bash .devcontainer/banco.sh demo
-- accounts: segundoadmin / admin456 (admin) and mariasilva / cliente123 (cliente)
-- the products have no photos, the upload is shown in the presentation

BEGIN;

-- hashes generated with jBCrypt
INSERT INTO usuario (nome, login, senha, email, endereco, admin) VALUES
    ('Segundo Administrador', 'segundoadmin',
     '$2a$12$YOQfcI62Xo8972qtgr1wNebmLTQTtXOM4eYGiqxjT58wT2O19N5B2',
     'segundoadmin@virtual.ufc.br', 'UFC Campus do Pici', TRUE),
    ('Maria Silva', 'mariasilva',
     '$2a$12$50KyOdJWVzIRdV.zTs3iGeh2J6G276wAXvtNxkZqKDTSO6zR8W.pe',
     'maria.silva@exemplo.com.br', 'Rua das Flores, 100, Fortaleza, CE', FALSE);

INSERT INTO categoria (nome) VALUES
    ('Livros'), ('Papelaria'), ('Informática');

-- finds the categories by name, so it doesn't depend on the generated ids
INSERT INTO produto (descricao, preco, quantidade, categoria_id)
SELECT p.descricao, p.preco, p.quantidade, c.id
FROM (VALUES
    ('Introdução à Programação para Web',  89.90, 12, 'Livros'),
    ('Design de Interação',               129.00,  5, 'Livros'),
    ('Caderno universitário 10 matérias',  32.50, 40, 'Papelaria'),
    ('Caneta esferográfica azul (kit 10)', 18.90, 25, 'Papelaria'),
    ('Mouse sem fio',                      59.90,  8, 'Informática'),
    ('Teclado mecânico compacto',         349.00,  0, 'Informática') -- no stock, not shown in the store
) AS p (descricao, preco, quantidade, categoria)
JOIN categoria c ON c.nome = p.categoria;

COMMIT;