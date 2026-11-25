package ufc.dao;

import ufc.modelo.Categoria;
import ufc.modelo.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados da tabela produto.
 * Usado pela vitrine (listarDisponiveis) e pelo CRUD de produtos do administrador.
 */
public class ProdutoDAO {

    // O JOIN traz o nome da categoria na mesma consulta, em vez de
    // fazer uma consulta extra para cada produto
    private static final String SELECT_COM_CATEGORIA =
            "SELECT p.id, p.descricao, p.preco, p.quantidade, p.foto, p.ativo, "
          + "       c.id AS categoria_id, c.nome AS categoria_nome "
          + "FROM produto p "
          + "JOIN categoria c ON c.id = p.categoria_id ";

    /**
     * Retorna os produtos ativos e com estoque, já com a categoria de cada um,
     * em ordem alfabética. Usado na vitrine (/loja).
     */
    public List<Produto> listarDisponiveis() {
        String sql = SELECT_COM_CATEGORIA + "WHERE p.ativo AND p.quantidade > 0 ORDER BY p.descricao";
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

    /**
     * Lista para o administrador: ativos = true traz os que estão na loja
     * (com ou sem estoque); ativos = false, os que saíram da loja.
     */
    public List<Produto> listar(boolean ativos) {
        String sql = SELECT_COM_CATEGORIA + "WHERE p.ativo = ? ORDER BY p.descricao";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativos);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            registrarErro("listar", e);
        }
        return produtos;
    }

    /** returns the product that matches the id (active or not), otherwise returns null */
    public Produto findById(int id) {
        String sql = SELECT_COM_CATEGORIA + "WHERE p.id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            registrarErro("findById", e);
            return null;
        }
    }

    /** inserts a new product, id provided by the db */
    public boolean insert(Produto produto) {
        String sql = "INSERT INTO produto (descricao, preco, quantidade, foto, categoria_id) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"id"})) {

            preencher(stmt, produto);
            if (stmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            registrarErro("insert", e);
            return false;
        }
    }

    /** updates a product's data (it doesn't change whether it's active) */
    public boolean update(Produto produto) {
        String sql = "UPDATE produto SET descricao = ?, preco = ?, quantidade = ?, foto = ?, categoria_id = ? "
                   + "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencher(stmt, produto);
            stmt.setInt(6, produto.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarErro("update", e);
            return false;
        }
    }

    /**
     * Exclui o produto. Como nas categorias, não consulta antes: tenta o DELETE
     * e deixa o banco decidir.
     *   nunca vendido   o DELETE funciona e o produto some do banco (EXCLUIDO)
     *   já vendido      o banco recusa (RESTRICT em item_venda) e o produto é
     *                   desativado: sai da loja e continua no histórico (DESATIVADO)
     * Assim o banco não acumula produtos que nunca venderam.
     */
    public ResultadoExclusao delete(int id) {
        try (Connection conn = ConnectionFactory.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM produto WHERE id = ?")) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0 ? ResultadoExclusao.EXCLUIDO : ResultadoExclusao.FALHOU;

            } catch (SQLException e) {
                if (!ErrosBanco.registroEmUso(e)) {
                    throw e;
                }
            }

            // Chegou aqui: o produto está em alguma venda. Não é erro, é a regra de negócio.
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE produto SET ativo = FALSE WHERE id = ?")) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0 ? ResultadoExclusao.DESATIVADO : ResultadoExclusao.FALHOU;
            }

        } catch (SQLException e) {
            registrarErro("delete", e);
            return ResultadoExclusao.FALHOU;
        }
    }

    /** Devolve à loja um produto desativado (por engano, ou que voltou a ser vendido). */
    public boolean reativar(int id) {
        String sql = "UPDATE produto SET ativo = TRUE WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarErro("reativar", e);
            return false;
        }
    }

    // Os cinco primeiros parâmetros são os mesmos no INSERT e no UPDATE
    private void preencher(PreparedStatement stmt, Produto produto) throws SQLException {
        stmt.setString(1, produto.getDescricao());
        stmt.setBigDecimal(2, produto.getPreco());
        stmt.setInt(3, produto.getQuantidade());
        if (produto.getFoto() == null) {
            stmt.setNull(4, Types.VARCHAR);
        } else {
            stmt.setString(4, produto.getFoto());
        }
        stmt.setInt(5, produto.getCategoria().getId());
    }

    // Converte a linha atual do ResultSet em um Produto com sua Categoria
    private Produto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("categoria_id"), rs.getString("categoria_nome"));
        Produto produto = new Produto(
                rs.getInt("id"),
                rs.getString("descricao"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade"),
                rs.getString("foto"),
                categoria
        );
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }

    private void registrarErro(String operacao, SQLException e) {
        System.err.println("Erro em ProdutoDAO." + operacao + " [SQLState " + e.getSQLState() + "]: "
                + e.getMessage());
    }
}
