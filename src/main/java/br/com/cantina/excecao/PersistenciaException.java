package br.com.cantina.excecao;

import java.sql.SQLException;

/**
 * Falha de acesso ao banco. Envolve a {@link SQLException} (checked) para que as camadas de cima
 * nao dependam de detalhes do JDBC. Erros de conexao (SQLState 08xxx) viram HTTP 503.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, SQLException causa) {
        super(mensagem, causa);
    }

    public boolean isFalhaDeConexao() {
        return getCause() instanceof SQLException sql
                && sql.getSQLState() != null
                && sql.getSQLState().startsWith("08");
    }
}
