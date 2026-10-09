package br.com.cantina.dao;

import br.com.cantina.dominio.Cliente;
import br.com.cantina.dominio.Pedido;
import br.com.cantina.dominio.Produto;
import br.com.cantina.dominio.SituacaoPedido;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PedidoDao extends JdbcDao {

    private static final String SELECT_COMPLETO = """
            SELECT pe.id, pe.preco_registrado, pe.data_pedido, pe.situacao,
                   c.id AS cliente_id, c.nome AS cliente_nome, c.email AS cliente_email,
                   pr.id AS produto_id, pr.nome AS produto_nome, pr.preco AS produto_preco, pr.estoque AS produto_estoque
              FROM pedido pe
              JOIN cliente c  ON c.id  = pe.cliente_id
              JOIN produto pr ON pr.id = pe.produto_id
            """;

    public PedidoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Pedido> listar() {
        return comConexao("listar pedidos", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(SELECT_COMPLETO + " ORDER BY pe.id");
                 ResultSet rs = ps.executeQuery()) {
                List<Pedido> pedidos = new ArrayList<>();
                while (rs.next()) {
                    pedidos.add(mapear(rs));
                }
                return pedidos;
            }
        });
    }

    public Optional<Pedido> buscarPorId(long id) {
        return buscar(id, SELECT_COMPLETO + " WHERE pe.id = ?");
    }

    /** Trava a linha do pedido: duas chamadas simultaneas de cancelamento nao repoem estoque duas vezes. */
    public Optional<Pedido> buscarPorIdParaAtualizacao(long id) {
        return buscar(id, SELECT_COMPLETO + " WHERE pe.id = ? FOR UPDATE OF pe");
    }

    public Pedido inserir(Pedido pedido) {
        return comConexao("inserir pedido", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement("""
                    INSERT INTO pedido (cliente_id, produto_id, preco_registrado, situacao)
                    VALUES (?, ?, ?, ?)
                    RETURNING id, data_pedido
                    """)) {
                ps.setLong(1, pedido.getCliente().getId());
                ps.setLong(2, pedido.getProduto().getId());
                ps.setBigDecimal(3, pedido.getPrecoRegistrado());
                ps.setString(4, pedido.getSituacao().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    pedido.registrarPersistencia(rs.getLong("id"), rs.getTimestamp("data_pedido").toLocalDateTime());
                }
                return pedido;
            }
        });
    }

    public void atualizarSituacao(long id, SituacaoPedido situacao) {
        comConexao("atualizar situacao do pedido", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement("UPDATE pedido SET situacao = ? WHERE id = ?")) {
                ps.setString(1, situacao.name());
                ps.setLong(2, id);
                return ps.executeUpdate();
            }
        });
    }

    private Optional<Pedido> buscar(long id, String sql) {
        return comConexao("buscar pedido", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
                }
            }
        });
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(rs.getLong("cliente_id"), rs.getString("cliente_nome"), rs.getString("cliente_email"));
        Produto produto = new Produto(rs.getLong("produto_id"), rs.getString("produto_nome"),
                rs.getBigDecimal("produto_preco"), rs.getInt("produto_estoque"));
        return new Pedido(
                rs.getLong("id"),
                cliente,
                produto,
                rs.getBigDecimal("preco_registrado"),
                rs.getTimestamp("data_pedido").toLocalDateTime(),
                SituacaoPedido.valueOf(rs.getString("situacao")));
    }
}
