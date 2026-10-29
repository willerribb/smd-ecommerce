package ufc.dao;

import ufc.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsuarioDAO {

    private static final String COLUNAS = "id, nome, login, senha, email, endereco, admin, ativo";

    /** insere um novo usuário com o id gerado pelo banco */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, login, senha, email, endereco, admin) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"id"})) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
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
            registrarErro("inserir", e);
            return false;
        }
    }

    /** atualiza os dados de um usuário *ativo*. sem alterar a senha -> temos o método alterarSenha. */
    public boolean atualizar(Usuario usuario) {
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
            registrarErro("atualizar", e);
            return false;
        }
    }

    /** troca a senha de um usuário ativo. */
    public boolean alterarSenha(int id, String novaSenha) {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenha);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarErro("alterarSenha", e);
            return false;
        }
    }

    /** exclusão de conta sem afetar o histórico de compras, de acordo com a lgpd
     *  o perfil é marcado como inativo e os dados pessoais são anonimizados:
     *  nome vira Usuário excluído
     *  login e email ambos viram excluido#ID
     *  o endereço é apagado
     *  a senha vira um valor aleatório */

    public boolean excluir(int id) {
        String sql = "UPDATE usuario SET ativo = FALSE, nome = 'Usuário excluído', "
                   + "login = 'excluido#' || id, email = 'excluido#' || id, "
                   + "endereco = NULL, senha = ? "
                   + "WHERE id = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarErro("excluir", e);
            return false;
        }
    }

    /**
     * retorna o usuário com o id informado ou null se não existir.
     * aqui, as contas inativas continuam aparecendo manter o histórico de vendas. */

    public Usuario buscarPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            registrarErro("buscarPorId", e);
            return null;
        }
    }

    /** retorna o usuário ativo com o login informado ou null se não existir. */
    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE login = ? AND ativo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }

        } catch (SQLException e) {
            registrarErro("buscarPorLogin", e);
            return null;
        }
    }

    /** autentica o usuário com login e senha corretos e conta ativa, ou null caso contrário. */
    // quando for implementar hash nas senhas, trocar a comparação por Bcrypt.checkpw(senha, usuario.getSenha())

    public Usuario autenticar(String login, String senha) {
        Usuario usuario = buscarPorLogin(login);
        if (usuario != null && usuario.isAtivo() && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    /** retorna todos os usuários ativos em ordem alfabética, por nome. */

    public List<Usuario> listar() {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE ativo ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }

        } catch (SQLException e) {
            registrarErro("listar", e);
        }
        return usuarios;
    }

    /** verifica se o login já está em uso (cadastro) */

    public boolean existeLogin(String login) {
        return existeLogin(login, 0);   // os ids começam em 1, então 0 não ignora ninguém
    }

    /** verifica se o login está em uso por outro usuário (edição de perfil) */

    public boolean existeLogin(String login, int idIgnorado) {
        return existe("SELECT 1 FROM usuario WHERE login = ? AND id <> ?", login, idIgnorado);
    }

    /** verifica se o e-mail já está em uso (cadastro). */

    public boolean existeEmail(String email) {
        return existeEmail(email, 0);
    }

    /** verifica se o e-mail está em uso por outro usuário (edição de perfil). */

    public boolean existeEmail(String email, int idIgnorado) {
        return existe("SELECT 1 FROM usuario WHERE email = ? AND id <> ?", email, idIgnorado);
    }

    // Executa uma consulta "SELECT 1 ... WHERE campo = ? AND id <> ?" e diz se encontrou algum registro

    private boolean existe(String sql, String valor, int idIgnorado) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, valor);
            stmt.setInt(2, idIgnorado);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            registrarErro("existe", e);
            return false;
        }
    }

    // Converte a linha atual do ResultSet em um objeto Usuario

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

    private void registrarErro(String operacao, SQLException e) {
        System.err.println("Erro em UsuarioDAO." + operacao + " [SQLState " + e.getSQLState() + "]: "
                + e.getMessage());
    }
}
