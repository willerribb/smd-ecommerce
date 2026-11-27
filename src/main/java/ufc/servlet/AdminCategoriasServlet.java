package ufc.servlet;

import ufc.dao.CategoriaDAO;
import ufc.modelo.Categoria;
import ufc.util.Sessao;
import ufc.util.ValidadorUsuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// admin category CRUD: form and list in the same page, the action comes in the "acao" parameter
@WebServlet("/admin/categorias")
public class AdminCategoriasServlet extends HttpServlet {

    private static final String PAGINA = "/WEB-INF/views/admin/categorias.jsp";
    private static final int TAMANHO_MAXIMO_NOME = 100; // same size as the db column

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String editar = request.getParameter("editar");
        if (editar != null) {
            Categoria categoria = categoriaDAO.findById(lerId(editar));
            if (categoria == null) {
                Sessao.definirMensagem(request, "warning", "Categoria não encontrada.");
                response.sendRedirect(request.getContextPath() + "/admin/categorias");
                return;
            }
            request.setAttribute("categoriaEditada", categoria);
        }
        mostrarPagina(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");
        if ("salvar".equals(acao)) {
            salvar(request, response);
        } else if ("excluir".equals(acao)) {
            excluir(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/categorias");
        }
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = lerId(request.getParameter("id")); // 0 = categoria nova
        String nome = ValidadorUsuario.limpar(request.getParameter("nome"));

        String erro = null;
        if (ValidadorUsuario.vazio(nome)) {
            erro = "Informe o nome da categoria.";
        } else if (nome.length() > TAMANHO_MAXIMO_NOME) {
            erro = "O nome da categoria deve ter até " + TAMANHO_MAXIMO_NOME + " caracteres.";
        } else if (categoriaDAO.existeNome(nome, id)) {
            erro = "Já existe uma categoria com esse nome.";
        }

        Categoria categoria = new Categoria(id, nome);
        if (erro == null) {
            boolean salvou = (id == 0) ? categoriaDAO.insert(categoria) : categoriaDAO.update(categoria);
            if (!salvou) {
                erro = "Não foi possível salvar agora. Tente novamente em alguns minutos.";
            }
        }

        if (erro != null) {
            // forward keeps what was typed in the form
            request.setAttribute("erro", erro);
            request.setAttribute("categoriaEditada", categoria);
            mostrarPagina(request, response);
            return;
        }

        Sessao.definirMensagem(request, "success",
                id == 0 ? "Categoria \"" + nome + "\" criada." : "Categoria \"" + nome + "\" atualizada.");
        response.sendRedirect(request.getContextPath() + "/admin/categorias");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (categoriaDAO.delete(lerId(request.getParameter("id")))) {
            case EXCLUIDO:
                Sessao.definirMensagem(request, "success", "Categoria excluída.");
                break;
            case EM_USO:
                Sessao.definirMensagem(request, "warning",
                        "Essa categoria tem produtos cadastrados (inclusive os que saíram da loja) "
                      + "e não pode ser excluída. Você pode renomeá-la.");
                break;
            default:
                Sessao.definirMensagem(request, "danger",
                        "Não foi possível excluir a categoria. Atualize a página e tente novamente.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/categorias");
    }

    private void mostrarPagina(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categorias", categoriaDAO.listar());
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    // anything that isn't a number becomes 0
    private int lerId(String valor) {
        try {
            return valor == null ? 0 : Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}