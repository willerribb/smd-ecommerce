package ufc.util;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

// product photos: checks, saves and deletes the files.
// they're saved outside the project (the target folder is wiped on every build),
// in the IUVE_FOTOS folder or in "iuve-fotos" inside the user's home folder.
// the db keeps "fotos/<uuid>.<ext>" and the FotoServlet serves them
public final class Fotos {

    public static final String PREFIXO = "fotos/";

    public static final long TAMANHO_MAXIMO = 2 * 1024 * 1024; // 2 MB

    // only names created by the system itself (uuid + extension) are accepted,
    // so something like /fotos/../../senhas.txt doesn't work
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

    // an empty file input also comes as a Part, with size 0
    public static boolean enviada(Part parte) {
        return parte != null && parte.getSize() > 0;
    }

    // checks the first bytes of the file to find out the image type, since the file name
    // and the content type can be faked. returns "jpg", "png", "webp" or null
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

    // saves the photo with a random name and returns the path to be stored in the db
    public static String salvar(Part parte, String extensao) throws IOException {
        Path pasta = pasta();
        Files.createDirectories(pasta);
        String nome = UUID.randomUUID() + "." + extensao;
        try (InputStream entrada = parte.getInputStream()) {
            Files.copy(entrada, pasta.resolve(nome));
        }
        return PREFIXO + nome;
    }

    // deletes the file of a photo uploaded by the site (paths that don't start with "fotos/" are ignored)
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
            // the product was already saved, so we just log it
            System.err.println("Não foi possível apagar a foto " + caminho + ": " + e.getMessage());
        }
    }
}