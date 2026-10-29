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

@WebServlet("/minha-conta/dados")
public class AtualizarDadosServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/minha-conta");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // id vem sempre da sessão, nunca do formulário.
        // assim ninguém consegue alterar os dados de outro usuário trocando um campo oculto
        Usuario logado = Sessao.getUsuarioLogado(request);

        String nome = ValidadorUsuario.limpar(request.getParameter("nome"));
        String login = ValidadorUsuario.limpar(request.getParameter("login"));
        String email = ValidadorUsuario.normalizarEmail(request.getParameter("email"));
        String endereco = ValidadorUsuario.limpar(request.getParameter("endereco"));

        String erro = ValidadorUsuario.validarDados(nome, login, email, endereco);
        if (erro == null && usuarioDAO.existeLogin(login, logado.getId())) {
            erro = "Esse login já está em uso. Escolha outro.";
        }
        if (erro == null && usuarioDAO.existeEmail(email, logado.getId())) {
            erro = "Esse e-mail já está em uso por outra conta.";
        }
        if (erro != null) {
            MinhaContaServlet.mostrarErro(request, response, "dados", erro);
            return;
        }

        Usuario atualizado = new Usuario(logado.getId(), nome, login, null,
                email, endereco, logado.isAdmin(), true);

        if (!usuarioDAO.atualizar(atualizado)) {
            MinhaContaServlet.mostrarErro(request, response, "dados",
                    "Não foi possível salvar agora. Tente novamente em alguns minutos.");
            return;
        }

        Sessao.atualizarUsuario(request, atualizado);   // mantém a sessão em dia
        Sessao.definirMensagem(request, "success", "Seus dados foram atualizados.");
        response.sendRedirect(request.getContextPath() + "/minha-conta");
    }
}
