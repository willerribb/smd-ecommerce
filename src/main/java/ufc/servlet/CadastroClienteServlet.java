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

@WebServlet("/cadastro")
public class CadastroClienteServlet extends HttpServlet {

    private static final String PAGINA = "/WEB-INF/views/cadastro.jsp";

    // O DAO não guarda estado, então uma única instância pode ser compartilhada
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

        String nome = ValidadorUsuario.limpar(request.getParameter("nome"));
        String login = ValidadorUsuario.limpar(request.getParameter("login"));
        String email = ValidadorUsuario.normalizarEmail(request.getParameter("email"));
        String endereco = ValidadorUsuario.limpar(request.getParameter("endereco"));
        String senha = request.getParameter("senha");                 // senha não leva trim
        String confirmacao = request.getParameter("confirmacaoSenha");

        String erro = ValidadorUsuario.validarDados(nome, login, email, endereco);
        if (erro == null) {
            erro = ValidadorUsuario.validarNovaSenha(senha, confirmacao);
        }
        if (erro == null && usuarioDAO.existeLogin(login)) {
            erro = "Esse login já está em uso. Escolha outro.";
        }
        if (erro == null && usuarioDAO.existeEmail(email)) {
            erro = "Já existe uma conta com esse e-mail. Se for sua, entre com seu login.";
        }

        if (erro != null) {
            request.setAttribute("erro", erro);
            request.getRequestDispatcher(PAGINA).forward(request, response);
            return;
        }


        Usuario novoCliente = new Usuario(nome, login, senha, email, endereco, false);

        if (usuarioDAO.inserir(novoCliente)) {
            Sessao.definirMensagem(request, "success", "Conta criada. Entre com seu login e senha.");
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            request.setAttribute("erro",
                    "Não foi possível concluir o cadastro agora. Tente novamente em alguns minutos.");
            request.getRequestDispatcher(PAGINA).forward(request, response);
        }
    }
}
