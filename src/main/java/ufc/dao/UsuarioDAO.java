package ufc.dao;

import ufc.modelo.Usuario;
import ufc.util.Senha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UsuarioDAO {

    private static final String COLUNAS = "id, nome, login, senha, email, endereco, admin, ativo";

    // inserts a new user, id provided by the db
    public boolean insert(Usuario usuario) {

        String sql = "INSERT INTO usuario (nome, login, senha, email, endereco, admin) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"id"})) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, Senha.generateHash(usuario.getSenha())); //generates the hash
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getEndereco());
            stmt.setBoolean(6, usuario.isAdmin());

            if (stmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.insert", e);
            return false;
        }
    }

    // updates a given active user's data. for password changing, we have a separate method.
    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, login = ?, email = ?, endereco = ?, admin = ? "
                   + "WHERE id = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getEndereco());
            stmt.setBoolean(5, usuario.isAdmin());
            stmt.setInt(6, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.update", e);
            return false;
        }
    }

    // changes an active user's password
    public boolean changePassword(int id, String novaSenha) {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Senha.generateHash(novaSenha));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.changePassword", e);
            return false;
        }
    }

    // this method is responsible for deleting a user account, according to best practices and respecting LGPD
    // the profile is flagged as inactive and personal data is anonymized
    public boolean deleteUser(int id) {
        String sql = "UPDATE usuario SET ativo = FALSE, nome = 'Usuário excluído', "
                   + "login = 'excluido#' || id, email = 'excluido#' || id, "
                   + "endereco = NULL, senha = ? "
                   + "WHERE id = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Senha.generateHash(UUID.randomUUID().toString()));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.deleteUser", e);
            return false;
        }
    }

    // returns the user that matches the id, otherwise returns null
    // inactive or deleted accounts still show up
    public Usuario findById(int id) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.findById", e);
            return null;
        }
    }

    // returns the user that matches the login (only active users), ignoring upper/lower case
    public Usuario findByLogin(String login) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE LOWER(login) = LOWER(?) AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.findByLogin", e);
            return null;
        }
    }

    // logs in/authenticates the user whose login information is correct and has an active account, otherwise returns null
    public Usuario authenticate(String login, String senha) {
        Usuario usuario = findByLogin(login);
        if (usuario != null && usuario.isAtivo() && Senha.match(senha, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }

    // counts the active admins, so the last one can't delete his own account
    public int contarAdminsAtivos() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE admin AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.contarAdminsAtivos", e);
            return 0; // on error, 0 makes the servlet refuse the delete
        }
    }

    // during user creation, checks if a username is already in use
    public boolean existeLogin(String login) {
        return existeLogin(login, 0);
    }

    // during profile edit, checks if a username is already in use (upper/lower case doesn't matter)
    public boolean existeLogin(String login, int idIgnorado) {
        return existe("SELECT 1 FROM usuario WHERE LOWER(login) = LOWER(?) AND id <> ?", login, idIgnorado);
    }

    // during user creation, checks if an email is already in use
    public boolean existeEmail(String email) {
        return existeEmail(email, 0);
    }

    // during profile edit, checks if an email is already in use
    public boolean existeEmail(String email, int idIgnorado) {
        return existe("SELECT 1 FROM usuario WHERE email = ? AND id <> ?", email, idIgnorado);
    }

    // does an sql search for user data
    private boolean existe(String sql, String valor, int idIgnorado) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, valor);
            stmt.setInt(2, idIgnorado);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            ErrosBanco.registrar("UsuarioDAO.existe", e);
            return false;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("email"),
                rs.getString("endereco"),
                rs.getBoolean("admin"),
                rs.getBoolean("ativo")
        );
    }
}
