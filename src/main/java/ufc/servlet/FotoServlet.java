package ufc.servlet;

import ufc.util.Fotos;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// serves the product photos (/fotos/<uuid>.jpg)
@WebServlet("/fotos/*")
public class FotoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String caminho = request.getPathInfo();
        String nome = caminho == null ? "" : caminho.substring(1);

        if (!Fotos.NOME_VALIDO.matcher(nome).matches()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path arquivo = Fotos.pasta().resolve(nome);
        if (!Files.isRegularFile(arquivo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String extensao = nome.substring(nome.lastIndexOf('.') + 1);
        response.setContentType("jpg".equals(extensao) ? "image/jpeg" : "image/" + extensao);
        response.setContentLengthLong(Files.size(arquivo));
        Files.copy(arquivo, response.getOutputStream());
    }
}