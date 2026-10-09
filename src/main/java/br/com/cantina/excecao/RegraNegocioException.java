package br.com.cantina.excecao;

/** Operacao viola uma regra de negocio ou um estado impossivel (ex.: sem estoque, cancelamento repetido). HTTP 409. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
