-- =====================================================================
-- IUVe: dados de demonstração (opcional)
--
-- Quando usar: logo depois de recriar o banco, para a loja não começar
-- vazia numa apresentação. Rodar duas vezes dá erro de nome repetido.
--   No Codespace:  bash .devcontainer/banco.sh demo
--   No pgAdmin:    Query Tool no ecommerce_db, abrir este arquivo, F5
--
-- Contas (além do iuveadmin / admin123, criado pelo createdb.sql):
--   segundoadmin / admin456     administrador, para demonstrar a exclusão
--                               da conta de admin (com um admin só, o
--                               sistema protege a conta e esconde o botão)
--   mariasilva   / cliente123   cliente
--
-- Os produtos não têm foto: as fotos são enviadas pelo site, e na
-- apresentação isso demonstra o upload.
-- =====================================================================

BEGIN;

-- Hashes BCrypt (custo 12, prefixo $2a$) gerados com a própria jBCrypt
INSERT INTO usuario (nome, login, senha, email, endereco, admin) VALUES
    ('Segundo Administrador', 'segundoadmin',
     '$2a$12$YOQfcI62Xo8972qtgr1wNebmLTQTtXOM4eYGiqxjT58wT2O19N5B2',
     'segundoadmin@virtual.ufc.br', 'UFC Campus do Pici', TRUE),
    ('Maria Silva', 'mariasilva',
     '$2a$12$50KyOdJWVzIRdV.zTs3iGeh2J6G276wAXvtNxkZqKDTSO6zR8W.pe',
     'maria.silva@exemplo.com.br', 'Rua das Flores, 100, Fortaleza, CE', FALSE);

INSERT INTO categoria (nome) VALUES
    ('Livros'), ('Papelaria'), ('Informática');

-- As categorias são buscadas pelo nome, para não depender dos ids gerados
INSERT INTO produto (descricao, preco, quantidade, categoria_id)
SELECT p.descricao, p.preco, p.quantidade, c.id
FROM (VALUES
    ('Introdução à Programação para Web',  89.90, 12, 'Livros'),
    ('Design de Interação',               129.00,  5, 'Livros'),
    ('Caderno universitário 10 matérias',  32.50, 40, 'Papelaria'),
    ('Caneta esferográfica azul (kit 10)', 18.90, 25, 'Papelaria'),
    ('Mouse sem fio',                      59.90,  8, 'Informática'),
    ('Teclado mecânico compacto',         349.00,  0, 'Informática')   -- sem estoque: fora da vitrine
) AS p (descricao, preco, quantidade, categoria)
JOIN categoria c ON c.nome = p.categoria;

COMMIT;
