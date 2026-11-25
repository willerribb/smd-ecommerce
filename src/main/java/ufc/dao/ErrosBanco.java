package ufc.dao;

import java.sql.SQLException;

/**
 * Reconhece, pelo SQLState, os erros do banco que não são falhas do sistema,
 * e sim regras de negócio que o próprio banco garante.
 *
 * Por que dois códigos: quando uma chave estrangeira com ON DELETE RESTRICT
 * impede um DELETE, algumas versões do PostgreSQL respondem 23503
 * (foreign_key_violation) e outras respondem 23001 (restrict_violation).
 * Os testes do projeto viram as duas: 23503 num PostgreSQL 16 e 23001 no
 * PostgreSQL do Windows. Conferir só um dos códigos fazia a exclusão
 * funcionar numa máquina e mostrar "erro" na outra.
 */
final class ErrosBanco {

    private static final String VIOLACAO_CHAVE_ESTRANGEIRA = "23503";
    private static final String VIOLACAO_RESTRICT = "23001";

    private ErrosBanco() {
        // só métodos estáticos
    }

    /**
     * true quando o banco recusou apagar o registro porque outro registro
     * ainda aponta para ele (uma categoria com produtos, um produto já vendido).
     */
    static boolean registroEmUso(SQLException e) {
        String estado = e.getSQLState();
        return VIOLACAO_CHAVE_ESTRANGEIRA.equals(estado) || VIOLACAO_RESTRICT.equals(estado);
    }
}
