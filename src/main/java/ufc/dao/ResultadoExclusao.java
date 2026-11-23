package ufc.dao;

/**
 * Resultado de uma tentativa de exclusão.
 *
 * Um boolean só diria "deu certo" ou "não deu". Na exclusão há mais casos,
 * e cada um pede uma mensagem diferente para o administrador:
 * a categoria pode estar em uso, e o produto pode ter sido só desativado.
 */
public enum ResultadoExclusao {

    /** A linha foi apagada do banco. */
    EXCLUIDO,

    /** Não podia ser apagada (está no histórico de vendas) e foi desativada. */
    DESATIVADO,

    /** Não pode ser apagada porque outros registros dependem dela. */
    EM_USO,

    /** O registro não existe ou houve erro de banco. */
    FALHOU
}
