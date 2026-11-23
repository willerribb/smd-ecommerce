package ufc.util;

import javax.servlet.http.HttpServletRequest;

public final class Navegacao {

    private Navegacao() {
    }

    /**
     * Devolve o destino recebido se ele for um caminho do próprio site;
     * senão (vazio, de outro site ou suspeito), devolve a loja.
     *
     * Protege contra "open redirect": sem essa conferência, alguém poderia
     * mandar um link como /login?destino=https://site-falso.com e o IUVe
     * levaria a pessoa até lá depois do login.
     */
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
