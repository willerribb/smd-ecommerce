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

// no-store on every page, so the back button doesn't show the user still logged in after logout.
// css, js and images don't change with the login, so they can stay in the cache
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