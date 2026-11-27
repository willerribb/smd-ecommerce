package ufc.servlet;

import ufc.util.Sessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// home page (login and signup)
@WebServlet("")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (Sessao.getUsuarioLogado(request) != null) {
            response.sendRedirect(request.getContextPath() + "/loja");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/entrada.jsp").forward(request, response);
    }
}