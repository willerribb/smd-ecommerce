package ufc.dao;

import ufc.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de banco de dados da tabela categoria.
 * Usado pelo CRUD de categorias do administrador e, depois,
 * pelo formulário de produtos (lista de categorias para escolher).
 */
public class CategoriaDAO {

    /**
     * SQLState que o PostgreSQL devolve quando uma chave estrangeira impede
     * a operação. Aqui: apagar uma categoria que ainda tem produtos
     * (produto.categoria_id tem ON DELETE RESTRICT).
     */
    private static final String VIOLACAO_CHAVE_ESTRANGEIRA = "23503";

    /** lists all categories in alphabetical order */
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
            registrarErro("listar", e);
        }
        return categorias;
    }

    /** returns the category that matches the id, otherwise returns null */
    public Categoria findById(int id) {
        String sql = "SELECT id, nome FROM categoria WHERE id = ?";

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

    /** inserts a new category, id provided by the db */
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
            registrarErro("insert", e);
            return false;
        }
    }

    /** renames a category */
    public boolean update(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarErro("update", e);
            return false;
        }
    }

    /**
     * Apaga a categoria. Em vez de consultar antes se ela tem produtos,
     * tenta apagar e deixa o banco recusar: a regra fica num lugar só
     * (o RESTRICT do createdb.sql) e não há intervalo entre a consulta
     * e o DELETE em que alguém poderia cadastrar um produto nela.
     */
    public ResultadoExclusao delete(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0 ? ResultadoExclusao.EXCLUIDO : ResultadoExclusao.FALHOU;

        } catch (SQLException e) {
            if (VIOLACAO_CHAVE_ESTRANGEIRA.equals(e.getSQLState())) {
                return ResultadoExclusao.EM_USO;   // não é erro do sistema, é regra de negócio
            }
            registrarErro("delete", e);
            return ResultadoExclusao.FALHOU;
        }
    }

    /**
     * Confere se já existe outra categoria com esse nome, sem diferenciar
     * maiúsculas e minúsculas (mesma regra do índice uk_categoria_nome).
     * idIgnorado: na edição, a própria categoria não conta; na criação, use 0.
     */
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
            registrarErro("existeNome", e);
            return false;
        }
    }

    // Converte a linha atual do ResultSet em uma Categoria
    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nome"));
    }

    private void registrarErro(String operacao, SQLException e) {
        System.err.println("Erro em CategoriaDAO." + operacao + " [SQLState " + e.getSQLState() + "]: "
                + e.getMessage());
    }
}
