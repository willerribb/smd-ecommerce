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

/**
 * Login pelo formulário da página de entrada ou pelo dropdown "Entrar" da navbar
 * (os dois usam o form-login.jspf).
 *
 *   GET   logado: vai para a loja; visitante: página de entrada no painel de login
 *   POST  sucesso: vai para o destino; erro: página de entrada no painel de login,
 *         com a mensagem, o nome de usuário preenchido e o destino guardado
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (Sessao.getUsuarioLogado(request) != null) {
            response.sendRedirect(request.getContextPath() + "/loja");
            return;
        }
        Sessao.abrirPainel(request, "login", null, null, null);
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String login = ValidadorUsuario.limpar(request.getParameter("login"));
        String senha = request.getParameter("senha");
        String destino = Navegacao.destinoSeguro(request, request.getParameter("destino"));

        Usuario usuario = null;
        if (!ValidadorUsuario.vazio(login) && !ValidadorUsuario.vazio(senha)) {
            usuario = usuarioDAO.authenticate(login, senha);
        }

        if (usuario == null) {
            // O erro sempre volta para a página de entrada, mesmo quando o login
            // foi tentado pelo dropdown da navbar; o destino vai junto, para a
            // pessoa voltar à página onde estava depois de acertar.
            Map<String, String> valores = new HashMap<>();
            valores.put("login", login);
            valores.put("destino", destino);
            Sessao.abrirPainel(request, "login", "danger", "Nome de usuário ou senha incorretos.", valores);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Sessao.iniciar(request, usuario);
        response.sendRedirect(destino);
    }
}
