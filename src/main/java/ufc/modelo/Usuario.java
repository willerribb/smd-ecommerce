package ufc.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String login;
    private String senha;      // implementar hash?
    private String email;
    private String endereco;
    private boolean admin;
    private boolean ativo = true;

    // usado pelo dao p/ montar o objeto
    public Usuario() {
    }

    // construtor p/ novos usuarios, id gerado pelo banco
    public Usuario(String nome, String login, String senha, String email,
                   String endereco, boolean admin) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.endereco = endereco;
        this.admin = admin;
    }

    // construtor p/ usuarios que ja existem no banco
    public Usuario(int id, String nome, String login, String senha, String email,
                   String endereco, boolean admin, boolean ativo) {
        this(nome, login, senha, email, endereco, admin);
        this.id = id;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPrimeiroNome() {
        if (nome == null || nome.trim().isEmpty()) {
            return "";
        }
        return nome.trim().split("\\s+")[0];
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario outro = (Usuario) o;
        return id != 0 && id == outro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', login='" + login
                + "', email='" + email + "', admin=" + admin + ", ativo=" + ativo + "}";
    }
}
