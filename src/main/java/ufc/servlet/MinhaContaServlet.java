package ufc.servlet;

import ufc.dao.UsuarioDAO;
import ufc.modelo.Usuario;
import ufc.util.Sessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/minha-conta")
public class MinhaContaServlet extends HttpServlet {

    static final String PAGINA = "/WEB-INF/views/minha-conta.jsp";

    static final String AVISO_ULTIMO_ADMIN = "Sua conta é a única de administrador e não pode ser excluída: "
            + "sem ela, ninguém mais gerencia a loja.";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarPagina(request, response);
    }

    // used by some servlets to show a validation error
    static void mostrarErro(HttpServletRequest request, HttpServletResponse response,
                            String secao, String erro) throws ServletException, IOException {
        request.setAttribute("secao", secao);
        request.setAttribute("erro", erro);
        mostrarPagina(request, response);
    }

    // for the last admin, the page shows the warning instead of the delete button
    private static void mostrarPagina(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario logado = Sessao.getUsuarioLogado(request);
        if (logado != null && logado.isAdmin() && new UsuarioDAO().contarAdminsAtivos() <= 1) {
            request.setAttribute("avisoUltimoAdmin", AVISO_ULTIMO_ADMIN);
        }
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }
}
