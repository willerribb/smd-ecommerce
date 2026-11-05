package ufc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final String URL;
    private static final String USUARIO;
    private static final String SENHA;

    static {
        Properties config = new Properties();
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("db.properties file NOT FOUND. Copy db.properties.example file from the /resources/ folder and fill in the appropriate fields.");
            }
            config.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read db.properties", e);
        }

        URL = config.getProperty("db.url");
        USUARIO = config.getProperty("db.usuario");
        SENHA = config.getProperty("db.senha");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC Driver not found.", e);
        }
    }

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
