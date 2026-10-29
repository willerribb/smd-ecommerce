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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String PAGINA = "/WEB-INF/views/login.jsp";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (Sessao.getUsuarioLogado(request) != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String login = ValidadorUsuario.limpar(request.getParameter("login"));
        String senha = request.getParameter("senha");

        Usuario usuario = null;
        if (!ValidadorUsuario.vazio(login) && !ValidadorUsuario.vazio(senha)) {
            usuario = usuarioDAO.autenticar(login, senha);
        }

        if (usuario == null) {
            request.setAttribute("erro", "Login ou senha incorretos.");
            request.getRequestDispatcher(PAGINA).forward(request, response);
            return;
        }

        Sessao.iniciar(request, usuario);
        response.sendRedirect(request.getContextPath() + "/");
    }
}
