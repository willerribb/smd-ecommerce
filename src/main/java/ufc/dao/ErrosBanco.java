package ufc.dao;

import java.sql.SQLException;

// prints db errors to the console, used by all the DAOs
final class ErrosBanco {

    private ErrosBanco() {
    }

    static void registrar(String onde, SQLException e) {
        System.err.println("Erro em " + onde + " [SQLState " + e.getSQLState() + "]: " + e.getMessage());
    }
}
