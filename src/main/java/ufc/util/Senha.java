package ufc.util;

import org.mindrot.jbcrypt.BCrypt;

public final class Senha {

    private static final int CUSTO = 12;

    private Senha() {
    }

    /** generates the hash string to be stored in the db */
    public static String generateHash (String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(CUSTO));
    }

    /** checks the password against the stored hash string */
    public static boolean match (String password, String hash) {
        if (password == null || hash == null) return false;

        try {
            return BCrypt.checkpw(password, hash);
        } catch (IllegalArgumentException e) {
            // checks whether what's stored inside the db is a valid Bcrypt hash, otherwise, returns false
            return false;
        }
    }
}
