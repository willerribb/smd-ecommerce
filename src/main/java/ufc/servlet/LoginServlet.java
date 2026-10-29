package ufc.servlet;

import ufc.dao.UsuarioDAO;
import ufc.modelo.Usuario;
import ufc.util.Navegacao;
import ufc.util.Sessao;
import ufc.util.ValidadorUsuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (Sessao.getUsuarioLogado(request) == null) {
            Sessao.abrirModal(request, "login", null, null, null);
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String login = ValidadorUsuario.limpar(request.getParameter("login"));
        String senha = request.getParameter("senha");
        String origem = Navegacao.destinoSeguro(request, request.getParameter("origem"));
        String destino = Navegacao.destinoSeguro(request, request.getParameter("destino"));

        Usuario usuario = null;
        if (!ValidadorUsuario.vazio(login) && !ValidadorUsuario.vazio(senha)) {
            usuario = usuarioDAO.autenticar(login, senha);
        }

        if (usuario == null) {
            Map<String, String> valores = new HashMap<>();
            valores.put("login", login);
            valores.put("destino", destino);
            Sessao.abrirModal(request, "login", "danger", "Login ou senha incorretos.", valores);
            response.sendRedirect(origem);
            return;
        }

        Sessao.iniciar(request, usuario);
        response.sendRedirect(destino);
    }
}
