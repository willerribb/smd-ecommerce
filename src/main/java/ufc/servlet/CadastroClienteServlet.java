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
 * Cadastro de cliente.
 *
 *   GET   logado: vai para a loja; visitante: página de entrada no painel de
 *         cadastro. Aceita ?destino=, enviado pelo "Criar conta" do dropdown.
 *   POST  sucesso: cria o cliente (admin sempre false), entra na conta e vai
 *         para o destino; erro: painel de cadastro com a mensagem e os campos
 *         preenchidos de novo (exceto as senhas)
 */
@WebServlet("/cadastro")
public class CadastroClienteServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (Sessao.getUsuarioLogado(request) != null) {
            response.sendRedirect(request.getContextPath() + "/loja");
            return;
        }

        Map<String, String> valores = new HashMap<>();
        String destino = request.getParameter("destino");
        if (destino != null) {
            valores.put("destino", Navegacao.destinoSeguro(request, destino));
        }
        Sessao.abrirPainel(request, "cadastro", null, null, valores);
        response.sendRedirect(request.getContextPath() + "/");
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
        String destino = Navegacao.destinoSeguro(request, request.getParameter("destino"));

        String erro = ValidadorUsuario.validarDados(nome, login, email, endereco);
        if (erro == null) {
            erro = ValidadorUsuario.validarNovaSenha(senha, confirmacao);
        }
        if (erro == null && usuarioDAO.existeLogin(login)) {
            erro = "Esse nome de usuário já está em uso. Escolha outro.";
        }
        if (erro == null && usuarioDAO.existeEmail(email)) {
            erro = "Já existe uma conta com esse e-mail. Se for sua, entre com seu nome de usuário.";
        }

        Usuario novoCliente = new Usuario(nome, login, senha, email, endereco, false);

        if (erro == null && !usuarioDAO.insert(novoCliente)) {
            erro = "Não foi possível concluir o cadastro agora. Tente novamente em alguns minutos.";
        }

        if (erro != null) {
            Map<String, String> valores = new HashMap<>();
            valores.put("nome", nome);
            valores.put("email", email);
            valores.put("endereco", endereco);
            valores.put("login", login);
            valores.put("destino", destino);
            Sessao.abrirPainel(request, "cadastro", "danger", erro, valores);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Único caso de login automático na plataforma
        Sessao.iniciar(request, novoCliente);
        Sessao.definirMensagem(request, "success",
                "Conta criada! Boas-vindas, " + novoCliente.getPrimeiroNome() + ".");
        response.sendRedirect(destino);
    }
}
