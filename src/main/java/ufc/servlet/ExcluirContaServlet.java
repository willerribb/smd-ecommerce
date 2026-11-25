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

@WebServlet("/minha-conta/excluir")
public class ExcluirContaServlet extends HttpServlet {

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
        String senha = request.getParameter("senha");

        String erro = null;
        if (logado.isAdmin() && usuarioDAO.contarAdminsAtivos() <= 1) {
            // A página já esconde o botão nesse caso; esta conferência vale para
            // quem enviar o formulário por outro caminho
            erro = MinhaContaServlet.AVISO_ULTIMO_ADMIN;
        } else if (ValidadorUsuario.vazio(senha) || usuarioDAO.authenticate(logado.getLogin(), senha) == null) {
            erro = "A senha informada está incorreta. Sua conta não foi excluída.";
        } else if (!usuarioDAO.deleteUser(logado.getId())) {
            erro = "Não foi possível excluir a conta agora. Tente novamente em alguns minutos.";
        }

        if (erro != null) {
            MinhaContaServlet.mostrarErro(request, response, "excluir", erro);
            return;
        }

        Sessao.encerrar(request);
        Sessao.definirMensagem(request, "info", "Sua conta foi excluída.");
        response.sendRedirect(request.getContextPath() + "/");
    }
}
