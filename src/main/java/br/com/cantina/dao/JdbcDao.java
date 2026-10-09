package br.com.cantina.dao;

import br.com.cantina.excecao.PersistenciaException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base dos DAOs: obtem a conexao e garante a liberacao.
 * <p>
 * {@link DataSourceUtils} devolve a MESMA conexao enquanto houver uma transacao aberta pelo Service
 * ({@code @Transactional}). Assim varios DAOs participam da mesma transacao: se uma etapa falha,
 * o rollback desfaz todas e nenhuma alteracao parcial permanece.
 */
abstract class JdbcDao {

    private final DataSource dataSource;

    protected JdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @FunctionalInterface
    protected interface OperacaoJdbc<T> {
        T executar(Connection conexao) throws SQLException;
    }

    protected <T> T comConexao(String descricao, OperacaoJdbc<T> operacao) {
        Connection conexao = DataSourceUtils.getConnection(dataSource);
        try {
            return operacao.executar(conexao);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao " + descricao, e);
        } finally {
            DataSourceUtils.releaseConnection(conexao, dataSource);
        }
    }
}
