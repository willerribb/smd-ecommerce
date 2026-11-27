package ufc.servlet;

import ufc.dao.ProdutoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// store page, open to everyone (logged in or not)
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