package ufc.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/minha-conta")
public class MinhaContaServlet extends HttpServlet {

    static final String PAGINA = "/WEB-INF/views/minha-conta.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    /**
     * Usado pelos servlets de /minha-conta/... quando há um erro de validação:
     * mostra o painel de novo com a mensagem na seção correspondente
     * ("dados", "senha" ou "excluir").
     */
    static void mostrarErro(HttpServletRequest request, HttpServletResponse response,
                            String secao, String erro) throws ServletException, IOException {
        request.setAttribute("secao", secao);
        request.setAttribute("erro", erro);
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }
}
