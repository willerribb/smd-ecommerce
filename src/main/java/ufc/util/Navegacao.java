package ufc.util;

import javax.servlet.http.HttpServletRequest;

public final class Navegacao {

    private Navegacao() {
    }

    public static String destinoSeguro(HttpServletRequest request, String destino) {
        String raiz = request.getContextPath();
        if (destino != null
                && destino.startsWith(raiz + "/")
                && !destino.contains("//")
                && !destino.contains("\\")) {
            return destino;
        }
        return raiz + "/";
    }
}
