package ufc.dao;

import ufc.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // lists all categories in alphabetical order
    public List<Categoria> listar() {
        String sql = "SELECT id, nome FROM categoria ORDER BY nome";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapear(rs));
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.listar", e);
        }
        return categorias;
    }

    // returns the category that matches the id, otherwise returns null
    public Categoria findById(int id) {
        String sql = "SELECT id, nome FROM categoria WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.findById", e);
            return null;
        }
    }

    // inserts a new category, id provided by the db
    public boolean insert(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"id"})) {

            stmt.setString(1, categoria.getNome());
            if (stmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    categoria.setId(chaves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.insert", e);
            return false;
        }
    }

    // renames a category
    public boolean update(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.update", e);
            return false;
        }
    }

    // a category with products can't be deleted, even if the products are inactive
    public ResultadoExclusao delete(int id) {
        if (temProdutos(id)) {
            return ResultadoExclusao.EM_USO;
        }

        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0 ? ResultadoExclusao.EXCLUIDO : ResultadoExclusao.FALHOU;

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.delete", e);
            return ResultadoExclusao.FALHOU;
        }
    }

    // checks if a category has products (active or not)
    private boolean temProdutos(int id) {
        String sql = "SELECT 1 FROM produto WHERE categoria_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.temProdutos", e);
            return false;
        }
    }

    // checks if there's another category with the same name, ignoring upper/lower case
    // idIgnorado: the category being edited (0 for a new one)
    public boolean existeNome(String nome, int idIgnorado) {
        String sql = "SELECT 1 FROM categoria WHERE LOWER(nome) = LOWER(?) AND id <> ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setInt(2, idIgnorado);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("CategoriaDAO.existeNome", e);
            return false;
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nome"));
    }
}
