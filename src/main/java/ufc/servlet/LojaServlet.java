package ufc.servlet;

import ufc.dao.ProdutoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Vitrine, aberta a visitantes e a quem já entrou.
 * Mostra só produtos ativos e com estoque (ProdutoDAO.listarDisponiveis).
 */
@WebServlet("/loja")
public class LojaServlet extends HttpServlet {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("produtos", produtoDAO.listarDisponiveis());
        request.getRequestDispatcher("/WEB-INF/views/loja.jsp").forward(request, response);
    }
}
