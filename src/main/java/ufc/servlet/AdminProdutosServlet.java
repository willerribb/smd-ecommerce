package ufc.servlet;

import ufc.dao.CategoriaDAO;
import ufc.dao.ProdutoDAO;
import ufc.modelo.Categoria;
import ufc.modelo.Produto;
import ufc.util.Fotos;
import ufc.util.Sessao;
import ufc.util.ValidadorUsuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

// admin product CRUD: form on top, active and inactive lists below
@WebServlet("/admin/produtos")
// tomcat upload size limit
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10 * 1024 * 1024, maxRequestSize = 11 * 1024 * 1024)
public class AdminProdutosServlet extends HttpServlet {

    private static final String PAGINA = "/WEB-INF/views/admin/produtos.jsp";
    private static final int TAMANHO_MAXIMO_DESCRICAO = 255; // same size as the db column
    // used by lerPreco
    private static final Pattern MILHAR_COM_PONTO = Pattern.compile("\\d{1,3}(\\.\\d{3})+");
    private static final Pattern NUMERO_COM_CENTAVOS = Pattern.compile("\\d+(\\.\\d{1,2})?");
    private static final BigDecimal PRECO_MAXIMO = new BigDecimal("99999999.99"); // NUMERIC(10,2)

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String editar = request.getParameter("editar");
        if (editar != null) {
            Produto produto = produtoDAO.findById(lerInteiro(editar));
            if (produto == null) {
                Sessao.definirMensagem(request, "warning", "Produto não encontrado.");
                response.sendRedirect(request.getContextPath() + "/admin/produtos");
                return;
            }
            request.setAttribute("produtoEditado", produto);
        }
        mostrarPagina(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // only the save form is multipart. getParts() throws an exception if the upload is over the tomcat limit, and then the whole form can't be read
        if (ehMultipart(request)) {
            try {
                request.getParts();
            } catch (IllegalStateException e) {
                Sessao.definirMensagem(request, "danger",
                        "A foto é grande demais. Escolha uma imagem de até 2 MB.");
                response.sendRedirect(request.getContextPath() + "/admin/produtos");
                return;
            }
        }

        String acao = request.getParameter("acao");
        if ("salvar".equals(acao)) {
            salvar(request, response);
        } else if ("excluir".equals(acao)) {
            excluir(request, response);
        } else if ("reativar".equals(acao)) {
            reativar(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/produtos");
        }
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = lerInteiro(request.getParameter("id")); // 0 = produto novo
        Produto atual = null;
        if (id > 0) {
            atual = produtoDAO.findById(id);
            if (atual == null) {
                Sessao.definirMensagem(request, "warning", "Produto não encontrado.");
                response.sendRedirect(request.getContextPath() + "/admin/produtos");
                return;
            }
        }

        String descricao = ValidadorUsuario.limpar(request.getParameter("descricao"));
        String textoPreco = ValidadorUsuario.limpar(request.getParameter("preco"));
        String textoQuantidade = ValidadorUsuario.limpar(request.getParameter("quantidade"));
        Categoria categoria = categoriaDAO.findById(lerInteiro(request.getParameter("categoria")));
        boolean removerFoto = request.getParameter("removerFoto") != null;
        Part parteFoto = ehMultipart(request) ? request.getPart("foto") : null;

        String erro = null;
        BigDecimal preco = lerPreco(textoPreco);
        int quantidade = lerInteiro(textoQuantidade, -1);

        if (ValidadorUsuario.vazio(descricao)) {
            erro = "Informe a descrição do produto.";
        } else if (descricao.length() > TAMANHO_MAXIMO_DESCRICAO) {
            erro = "A descrição deve ter até " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.";
        } else if (categoria == null) {
            erro = "Escolha uma categoria.";
        } else if (preco == null) {
            erro = "Informe um preço válido, com no máximo duas casas decimais.";
        } else if (preco.signum() < 0 || preco.compareTo(PRECO_MAXIMO) > 0) {
            erro = "O preço deve ficar entre R$ 0,00 e R$ 99.999.999,99.";
        } else if (quantidade < 0) {
            erro = "Informe a quantidade em estoque: um número inteiro, zero ou maior.";
        }

        String extensao = null;
        if (erro == null && Fotos.enviada(parteFoto)) {
            if (parteFoto.getSize() > Fotos.TAMANHO_MAXIMO) {
                erro = "A foto deve ter até 2 MB.";
            } else if ((extensao = Fotos.extensao(parteFoto)) == null) {
                erro = "A foto deve ser uma imagem JPG, PNG ou WebP.";
            }
        }

        // saves the photo first, then the product
        String fotoNova = null;
        if (erro == null && extensao != null) {
            try {
                fotoNova = Fotos.salvar(parteFoto, extensao);
            } catch (IOException e) {
                System.err.println("Erro ao gravar a foto em " + Fotos.pasta() + ": " + e.getMessage());
                erro = "Não foi possível salvar a foto agora. Tente novamente em alguns minutos.";
            }
        }

        String fotoAnterior = atual != null ? atual.getFoto() : null;
        Produto produto = null;
        if (erro == null) {
            String foto = fotoNova != null ? fotoNova : (removerFoto ? null : fotoAnterior);
            produto = new Produto(id, descricao, preco, quantidade, foto, categoria);
            boolean salvou = (id == 0) ? produtoDAO.insert(produto) : produtoDAO.update(produto);
            if (!salvou) {
                Fotos.excluir(fotoNova); // the product wasn't saved, so the new photo isn't needed
                erro = "Não foi possível salvar agora. Tente novamente em alguns minutos.";
            }
        }

        if (erro != null) {
            // forward keeps what was typed (the jsp reads it from ${param}).
            // the chosen file is lost, a file input can't be filled again
            request.setAttribute("erro", erro);
            request.setAttribute("produtoEditado", atual);
            mostrarPagina(request, response);
            return;
        }

        // the old photo is only deleted after the product is updated
        if (fotoAnterior != null && !fotoAnterior.equals(produto.getFoto())) {
            Fotos.excluir(fotoAnterior);
        }

        Sessao.definirMensagem(request, "success", id == 0
                ? "Produto \"" + descricao + "\" criado."
                : "Produto \"" + descricao + "\" atualizado.");
        response.sendRedirect(request.getContextPath() + "/admin/produtos");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Produto produto = produtoDAO.findById(lerInteiro(request.getParameter("id")));
        if (produto == null) {
            Sessao.definirMensagem(request, "danger",
                    "Não foi possível excluir o produto. Atualize a página e tente novamente.");
            response.sendRedirect(request.getContextPath() + "/admin/produtos");
            return;
        }

        switch (produtoDAO.delete(produto.getId())) {
            case EXCLUIDO:
                Fotos.excluir(produto.getFoto());
                Sessao.definirMensagem(request, "success", "Produto excluído.");
                break;
            case DESATIVADO:
                // keeps the photo, the product can be reactivated
                Sessao.definirMensagem(request, "info",
                        "\"" + produto.getDescricao() + "\" já aparece em compras, então saiu da loja "
                      + "mas continua no histórico. Ele está em \"Inativos\", onde pode ser reativado.");
                break;
            default:
                Sessao.definirMensagem(request, "danger",
                        "Não foi possível excluir o produto. Atualize a página e tente novamente.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/produtos");
    }

    private void reativar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Produto produto = produtoDAO.findById(lerInteiro(request.getParameter("id")));
        if (produto != null && produtoDAO.reativar(produto.getId())) {
            String texto = "\"" + produto.getDescricao() + "\" voltou para a loja.";
            if (produto.getQuantidade() == 0) {
                texto += " Ele só aparece na vitrine quando tiver estoque.";
            }
            Sessao.definirMensagem(request, "success", texto);
        } else {
            Sessao.definirMensagem(request, "danger",
                    "Não foi possível reativar o produto. Atualize a página e tente novamente.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/produtos");
    }

    private void mostrarPagina(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categorias", categoriaDAO.listar());
        request.setAttribute("produtosAtivos", produtoDAO.listar(true));
        request.setAttribute("produtosInativos", produtoDAO.listar(false));
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    private boolean ehMultipart(HttpServletRequest request) {
        String tipo = request.getContentType();
        return tipo != null && tipo.toLowerCase().startsWith("multipart/");
    }

    private BigDecimal lerPreco(String texto) {
        if (ValidadorUsuario.vazio(texto)) {
            return null;
        }
        String limpo = texto.replace(" ", "");
        String normalizado;
        if (limpo.contains(",")) {
            normalizado = limpo.replace(".", "").replace(",", ".");
        } else if (MILHAR_COM_PONTO.matcher(limpo).matches()) {
            normalizado = limpo.replace(".", "");
        } else {
            normalizado = limpo;
        }
        // blocks things like "1e5" or "-3", that BigDecimal would accept
        if (!NUMERO_COM_CENTAVOS.matcher(normalizado).matches()) {
            return null;
        }
        return new BigDecimal(normalizado);
    }

    // anything that isn't a number becomes 0
    private int lerInteiro(String valor) {
        return lerInteiro(valor, 0);
    }

    private int lerInteiro(String valor, int seInvalido) {
        try {
            return valor == null ? seInvalido : Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return seInvalido;
        }
    }
}