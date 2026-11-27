package ufc.dao;

// result of a delete. a boolean isn't enough here, each case shows a different message to the admin
public enum ResultadoExclusao {

    EXCLUIDO, // removed from the db

    DESATIVADO, // product that was already sold, only leaves the store

    EM_USO, // category that still has products

    FALHOU // not found or db error
}
