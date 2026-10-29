package ufc.servlet;

import ufc.dao.UsuarioDAO;
import ufc.modelo.Usuario;
import ufc.util.Sessao;
import ufc.util.ValidadorUsuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/minha-conta/senha")
public class AlterarSenhaServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/minha-conta");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario logado = Sessao.getUsuarioLogado(request);
        String senhaAtual = request.getParameter("senhaAtual");
        String novaSenha = request.getParameter("novaSenha");
        String confirmacao = request.getParameter("confirmacaoSenha");

        String erro = null;
        if (ValidadorUsuario.vazio(senhaAtual)
                || usuarioDAO.autenticar(logado.getLogin(), senhaAtual) == null) {
            erro = "A senha atual está incorreta.";
        }
        if (erro == null) {
            erro = ValidadorUsuario.validarNovaSenha(novaSenha, confirmacao);
        }
        if (erro == null && !usuarioDAO.alterarSenha(logado.getId(), novaSenha)) {
            erro = "Não foi possível alterar a senha agora. Tente novamente em alguns minutos.";
        }

        if (erro != null) {
            MinhaContaServlet.mostrarErro(request, response, "senha", erro);
            return;
        }

        Sessao.definirMensagem(request, "success", "Sua senha foi alterada.");
        response.sendRedirect(request.getContextPath() + "/minha-conta");
    }
}
