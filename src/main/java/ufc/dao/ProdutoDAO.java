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

public class ProdutoDAO {

    // every select brings the category name with the JOIN
    private static final String SELECT_COM_CATEGORIA =
            "SELECT p.id, p.descricao, p.preco, p.quantidade, p.foto, p.ativo, "
          + "       c.id AS categoria_id, c.nome AS categoria_nome "
          + "FROM produto p "
          + "JOIN categoria c ON c.id = p.categoria_id ";

    // store page: active products with stock, in alphabetical order
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
            ErrosBanco.registrar("ProdutoDAO.listarDisponiveis", e);
        }
        return produtos;
    }

    // admin page: active products (with or without stock) or inactive ones
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
            ErrosBanco.registrar("ProdutoDAO.listar", e);
        }
        return produtos;
    }

    // returns the product that matches the id (active or not), otherwise returns null
    public Produto findById(int id) {
        String sql = SELECT_COM_CATEGORIA + "WHERE p.id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("ProdutoDAO.findById", e);
            return null;
        }
    }

    // inserts a new product, id provided by the db
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
            ErrosBanco.registrar("ProdutoDAO.insert", e);
            return false;
        }
    }

    // updates a product's data (it doesn't change whether it's active)
    public boolean update(Produto produto) {
        String sql = "UPDATE produto SET descricao = ?, preco = ?, quantidade = ?, foto = ?, categoria_id = ? "
                   + "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencher(stmt, produto);
            stmt.setInt(6, produto.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("ProdutoDAO.update", e);
            return false;
        }
    }

    // a product that was already sold stays in the db because of the purchase history,
    // so it's only deactivated. one that was never sold is deleted for real
    public ResultadoExclusao delete(int id) {
        boolean vendido = foiVendido(id);
        String sql = vendido
                ? "UPDATE produto SET ativo = FALSE WHERE id = ?"
                : "DELETE FROM produto WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                return ResultadoExclusao.FALHOU;
            }
            return vendido ? ResultadoExclusao.DESATIVADO : ResultadoExclusao.EXCLUIDO;

        } catch (SQLException e) {
            ErrosBanco.registrar("ProdutoDAO.delete", e);
            return ResultadoExclusao.FALHOU;
        }
    }

    // puts a deactivated product back in the store
    public boolean reativar(int id) {
        String sql = "UPDATE produto SET ativo = TRUE WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("ProdutoDAO.reativar", e);
            return false;
        }
    }

    // checks if the product shows up in any purchase
    private boolean foiVendido(int id) {
        String sql = "SELECT 1 FROM item_venda WHERE produto_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("ProdutoDAO.foiVendido", e);
            return false;
        }
    }

    // same 5 parameters for insert and update
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
}
