package ufc.filtro;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Pede ao navegador que não guarde nenhuma página do site (Cache-Control: no-store).
 *
 * O problema que resolve: depois de sair da conta, o botão Voltar mostrava a
 * loja como se a pessoa ainda estivesse logada ("Olá, Maria" na navbar). Não era
 * a sessão que continuava viva; era o navegador exibindo a cópia que tinha
 * guardado, sem perguntar nada ao servidor. Com no-store não existe cópia, e o
 * Voltar pede a página de novo, já sem a sessão.
 *
 * Antes, só o AutenticacaoFilter mandava esse cabeçalho, e só nas páginas
 * restritas. Toda página com a navbar mostra quem está logado, então todas
 * precisam dele. O AutenticacaoFilter continua mandando o mesmo cabeçalho;
 * a repetição não causa problema.
 *
 * Arquivos estáticos (CSS, JavaScript, imagens, fotos) ficam de fora: eles não
 * mudam com o login e devem continuar em cache, senão o site fica mais lento.
 * O FotoServlet define o cache das fotos por conta própria.
 */
@WebFilter("/*")
public class SemCacheFilter implements Filter {

    private static final String[] PASTAS_ESTATICAS = {"/css/", "/js/", "/vendor/", "/img/", "/fotos/"};

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String caminho = request.getRequestURI().substring(request.getContextPath().length());

        if (!ehEstatico(caminho)) {
            ((HttpServletResponse) resp).setHeader("Cache-Control", "no-store");
        }
        chain.doFilter(req, resp);
    }

    private boolean ehEstatico(String caminho) {
        for (String pasta : PASTAS_ESTATICAS) {
            if (caminho.startsWith(pasta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
