package ufc.util;

import java.util.regex.Pattern;

/**
 * Regras de validação dos dados de usuário, usadas tanto no cadastro
 * quanto na edição do perfil. Cada método retorna a mensagem do primeiro
 * erro encontrado (pronta para exibir na página), ou null se estiver tudo certo.
 */
public final class ValidadorUsuario {

    private static final Pattern FORMATO_LOGIN = Pattern.compile("[A-Za-z0-9._-]{3,50}");
    private static final Pattern FORMATO_EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    public static final int TAMANHO_MINIMO_SENHA = 6;

    private ValidadorUsuario() {
    }

    /** Valida nome, login, e-mail e endereço (limites iguais aos das colunas do banco). */
    public static String validarDados(String nome, String login, String email, String endereco) {
        if (vazio(nome) || vazio(login) || vazio(email) || vazio(endereco)) {
            return "Preencha todos os campos.";
        }
        if (nome.length() > 150 || endereco.length() > 255) {
            return "O nome deve ter até 150 caracteres e o endereço até 255.";
        }
        if (!FORMATO_LOGIN.matcher(login).matches()) {
            return "O login deve ter de 3 a 50 caracteres, usando apenas letras, números, "
                 + "ponto, hífen ou sublinhado.";
        }
        if (email.length() > 150 || !FORMATO_EMAIL.matcher(email).matches()) {
            return "Informe um e-mail válido.";
        }
        return null;
    }

    /** Valida uma nova senha e sua confirmação. */
    public static String validarNovaSenha(String senha, String confirmacao) {
        if (senha == null || senha.isEmpty()) {
            return "Preencha todos os campos.";
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            return "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.";
        }
        if (!senha.equals(confirmacao)) {
            return "As senhas digitadas não são iguais.";
        }
        return null;
    }

    /** Remove espaços do início e do fim (senhas não devem passar por aqui). */
    public static String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    /** Remove espaços e deixa em minúsculas, para que Joao@Email.com e joao@email.com sejam iguais. */
    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public static boolean vazio(String valor) {
        return valor == null || valor.isEmpty();
    }
}
