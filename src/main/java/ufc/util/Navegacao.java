package ufc.util;

import javax.servlet.http.HttpServletRequest;

public final class Navegacao {

    private Navegacao() {
    }

    // only goes back to a page of the site itself, otherwise goes to the store
    // (so a link like /login?destino=https://site-falso.com doesn't take the user to another site)
    public static String destinoSeguro(HttpServletRequest request, String destino) {
        String raiz = request.getContextPath();
        if (destino != null
                && destino.startsWith(raiz + "/")
                && !destino.contains("//")
                && !destino.contains("\\")) {
            return destino;
        }
        return raiz + "/loja";
    }
}