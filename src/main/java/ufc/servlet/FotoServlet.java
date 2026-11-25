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

/**
 * Entrega as fotos dos produtos, que ficam fora da pasta da aplicação (ver Fotos).
 *   GET /fotos/xxxx.jpg
 * Aberto a todos, como a vitrine.
 */
@WebServlet("/fotos/*")
public class FotoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // getPathInfo é o que vem depois de /fotos: "/xxxx.jpg"
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
        // O nome de uma foto nunca muda de conteúdo (uma foto nova ganha outro nome),
        // então o navegador pode guardá-la por um ano sem perguntar de novo
        response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
        // Impede o navegador de "adivinhar" outro tipo para o arquivo
        response.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(arquivo, response.getOutputStream());
    }
}
