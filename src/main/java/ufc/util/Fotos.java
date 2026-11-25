package ufc.util;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Fotos dos produtos: validação, gravação e exclusão dos arquivos.
 *
 * As fotos ficam numa pasta FORA do projeto. A pasta da aplicação no Tomcat é
 * a do artefato (target/...), que o Maven e o IntelliJ apagam ao reconstruir;
 * fotos gravadas lá sumiriam. Quem entrega as fotos ao navegador é o FotoServlet,
 * no endereço /fotos/nome.jpg.
 *
 * Pasta: a variável de ambiente IUVE_FOTOS, se existir; senão, "iuve-fotos" na
 * pasta do usuário (C:\Users\nome\iuve-fotos no Windows, /home/codespace/iuve-fotos
 * no Codespaces). Ela é criada na primeira foto enviada.
 *
 * No banco, produto.foto guarda "fotos/nome.jpg": um caminho relativo à raiz do
 * site, como as imagens da pasta webapp. Por isso a JSP monta ${raiz}/${produto.foto}
 * do mesmo jeito, venha a foto de onde vier.
 */
public final class Fotos {

    /** Início do caminho gravado no banco para as fotos enviadas pelo site. */
    public static final String PREFIXO = "fotos/";

    public static final long TAMANHO_MAXIMO = 2 * 1024 * 1024;   // 2 MB

    /**
     * Nome que o próprio sistema dá aos arquivos: um UUID e a extensão.
     * O FotoServlet só entrega arquivos com nomes assim, o que impede pedidos
     * como /fotos/../../senhas.txt (path traversal).
     */
    public static final Pattern NOME_VALIDO = Pattern.compile(
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)");

    private Fotos() {
    }

    public static Path pasta() {
        String configurada = System.getenv("IUVE_FOTOS");
        if (configurada != null && !configurada.trim().isEmpty()) {
            return Paths.get(configurada.trim());
        }
        return Paths.get(System.getProperty("user.home"), "iuve-fotos");
    }

    /** true se o formulário trouxe um arquivo (o campo vazio também chega como Part, com tamanho 0). */
    public static boolean enviada(Part parte) {
        return parte != null && parte.getSize() > 0;
    }

    /**
     * Descobre o tipo da imagem pelos primeiros bytes do arquivo ("assinatura"),
     * e não pelo nome nem pelo tipo informado pelo navegador, que podem ser
     * trocados por quem envia. Devolve a extensão ("jpg", "png" ou "webp"),
     * ou null se não for uma imagem aceita.
     */
    public static String extensao(Part parte) throws IOException {
        byte[] b = new byte[12];
        int lidos;
        try (InputStream entrada = parte.getInputStream()) {
            lidos = entrada.readNBytes(b, 0, b.length);
        }
        if (lidos >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (lidos >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (lidos >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }

    /**
     * Grava a foto com um nome aleatório e devolve o caminho para o banco
     * ("fotos/xxxx.jpg"). O nome original do arquivo nunca é usado: ele pode
     * ter acentos, espaços, barras ou repetir o nome de outra foto.
     */
    public static String salvar(Part parte, String extensao) throws IOException {
        Path pasta = pasta();
        Files.createDirectories(pasta);
        String nome = UUID.randomUUID() + "." + extensao;
        try (InputStream entrada = parte.getInputStream()) {
            Files.copy(entrada, pasta.resolve(nome));
        }
        return PREFIXO + nome;
    }

    /**
     * Apaga o arquivo de uma foto enviada pelo site. Caminhos que não começam
     * com "fotos/" (por exemplo, imagens de exemplo dentro de webapp) nunca são apagados.
     */
    public static void excluir(String caminho) {
        if (caminho == null || !caminho.startsWith(PREFIXO)) {
            return;
        }
        String nome = caminho.substring(PREFIXO.length());
        if (!NOME_VALIDO.matcher(nome).matches()) {
            return;
        }
        try {
            Files.deleteIfExists(pasta().resolve(nome));
        } catch (IOException e) {
            // A foto fica órfã na pasta, mas o produto já foi salvo: não vale interromper por isso
            System.err.println("Não foi possível apagar a foto " + caminho + ": " + e.getMessage());
        }
    }
}
