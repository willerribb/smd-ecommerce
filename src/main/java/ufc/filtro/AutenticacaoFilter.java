package ufc.filtro;

import ufc.modelo.Usuario;
import ufc.util.Sessao;

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
import java.util.HashMap;
import java.util.Map;


@WebFilter({"/minha-conta", "/minha-conta/*", "/admin/*"})
public class AutenticacaoFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        response.setHeader("Cache-Control", "no-store");

        Usuario usuario = Sessao.getUsuarioLogado(request);
        if (usuario == null) {
            Map<String, String> valores = new HashMap<>();
            valores.put("destino", "GET".equals(request.getMethod())
                    ? request.getRequestURI()
                    : request.getContextPath() + "/minha-conta");
            Sessao.abrirModal(request, "login", "warning", "Entre na sua conta para continuar.", valores);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        if (caminho.startsWith("/admin/") && !usuario.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/minha-conta");
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
