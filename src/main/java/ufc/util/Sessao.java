package ufc.util;

import ufc.modelo.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public final class Sessao {

    public static final String USUARIO_LOGADO = "usuarioLogado";

    public static final String MENSAGEM = "mensagem";
    public static final String TIPO_MENSAGEM = "tipoMensagem";

    public static final String MODAL = "modal";
    public static final String MENSAGEM_MODAL = "mensagemModal";
    public static final String TIPO_MENSAGEM_MODAL = "tipoMensagemModal";
    public static final String VALORES_MODAL = "valoresModal";

    private static final int TEMPO_LIMITE_MINUTOS = 30;

    private Sessao() {
    }

    /** retorna o usuario logado ou null */
    public static Usuario getUsuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);   // false: não cria sessão nova
        return sessao == null ? null : (Usuario) sessao.getAttribute(USUARIO_LOGADO);
    }

    /** descarta a sessao anterior e inicia uma nova */
    public static void iniciar(HttpServletRequest request, Usuario usuario) {
        encerrar(request);
        HttpSession nova = request.getSession(true);
        nova.setMaxInactiveInterval(TEMPO_LIMITE_MINUTOS * 60);
        atualizarUsuario(request, usuario);
    }

    /** atualiza o usuario da sessão */
    public static void atualizarUsuario(HttpServletRequest request, Usuario usuario) {
        usuario.setSenha(null);   // a senha nunca fica guardada na sessão
        request.getSession().setAttribute(USUARIO_LOGADO, usuario);
    }

    /** encerra a sessão atual */
    public static void encerrar(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }

    public static void definirMensagem(HttpServletRequest request, String tipo, String texto) {
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(TIPO_MENSAGEM, tipo);
        sessao.setAttribute(MENSAGEM, texto);
    }

    public static void abrirModal(HttpServletRequest request, String modal, String tipo,
                                  String mensagem, Map<String, String> valores) {
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(MODAL, modal);
        sessao.setAttribute(TIPO_MENSAGEM_MODAL, tipo);
        sessao.setAttribute(MENSAGEM_MODAL, mensagem);
        sessao.setAttribute(VALORES_MODAL, valores != null ? valores : new HashMap<String, String>());
    }
}
