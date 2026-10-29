package ufc.dao;

import ufc.modelo.Categoria;
import ufc.modelo.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados da tabela produto.
 * Por enquanto só a listagem da vitrine; o cadastro de produtos
 * (área do administrador) vem nas próximas entregas.
 */
public class ProdutoDAO {

    /**
     * Retorna os produtos com estoque disponível, já com a categoria de cada um,
     * em ordem alfabética. Usado na vitrine da home.
     */
    public List<Produto> listarDisponiveis() {
        // O JOIN traz o nome da categoria na mesma consulta, em vez de
        // fazer uma consulta extra para cada produto
        String sql = "SELECT p.id, p.descricao, p.preco, p.quantidade, p.foto, "
                   + "       c.id AS categoria_id, c.nome AS categoria_nome "
                   + "FROM produto p "
                   + "JOIN categoria c ON c.id = p.categoria_id "
                   + "WHERE p.quantidade > 0 "
                   + "ORDER BY p.descricao";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapear(rs));
            }

        } catch (SQLException e) {
            registrarErro("listarDisponiveis", e);
        }
        return produtos;
    }

    // Converte a linha atual do ResultSet em um Produto com sua Categoria
    private Produto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("categoria_id"), rs.getString("categoria_nome"));
        return new Produto(
                rs.getInt("id"),
                rs.getString("descricao"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade"),
                rs.getString("foto"),
                categoria
        );
    }

    private void registrarErro(String operacao, SQLException e) {
        System.err.println("Erro em ProdutoDAO." + operacao + " [SQLState " + e.getSQLState() + "]: "
                + e.getMessage());
    }
}
