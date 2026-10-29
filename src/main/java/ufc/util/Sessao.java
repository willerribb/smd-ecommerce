package ufc.util;

import ufc.modelo.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public final class Sessao {

    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String MENSAGEM = "mensagem";
    public static final String TIPO_MENSAGEM = "tipoMensagem";
    private static final int TEMPO_LIMITE_MINUTOS = 30;

    private Sessao() {
    }

    /** Retorna o usuário logado, ou null se não houver ninguém logado. */
    public static Usuario getUsuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);   // false: não cria sessão nova
        return sessao == null ? null : (Usuario) sessao.getAttribute(USUARIO_LOGADO);
    }

    /**
     * Inicia a sessão após um login bem-sucedido. A sessão anterior é
     * descartada e uma nova é criada, o que impede que alguém que conhecia
     * o identificador da sessão antiga "herde" o login (session fixation).
     */
    public static void iniciar(HttpServletRequest request, Usuario usuario) {
        encerrar(request);
        HttpSession nova = request.getSession(true);
        nova.setMaxInactiveInterval(TEMPO_LIMITE_MINUTOS * 60);
        atualizarUsuario(request, usuario);
    }

    /** Substitui o usuário guardado na sessão (por exemplo, após editar o perfil). */
    public static void atualizarUsuario(HttpServletRequest request, Usuario usuario) {
        usuario.setSenha(null);   // a senha nunca fica guardada na sessão
        request.getSession().setAttribute(USUARIO_LOGADO, usuario);
    }

    /** Encerra a sessão atual, se existir. */
    public static void encerrar(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }

    /**
     * Guarda uma mensagem para a PRÓXIMA página, usada antes de um
     * redirecionamento ("Seus dados foram atualizados", por exemplo).
     * O fragmento mensagens.jspf exibe a mensagem uma única vez e a remove.
     *
     * @param tipo classe de alerta do Bootstrap: success, info, warning ou danger
     */
    public static void definirMensagem(HttpServletRequest request, String tipo, String texto) {
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(TIPO_MENSAGEM, tipo);
        sessao.setAttribute(MENSAGEM, texto);
    }
}
