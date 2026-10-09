package br.com.cantina.excecao;

/** Referencia a um registro inexistente (cliente, produto ou pedido). HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " " + id + " nao encontrado");
    }
}
