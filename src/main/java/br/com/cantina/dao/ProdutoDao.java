package br.com.cantina.dao;

import br.com.cantina.dominio.Produto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** CRUD do recurso principal. Somente SQL aqui: as regras ficam no Service/dominio. */
@Repository
public class ProdutoDao extends JdbcDao {

    private static final String COLUNAS = "SELECT id, nome, preco, estoque FROM produto";

    public ProdutoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Produto> listar() {
        return comConexao("listar produtos", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(COLUNAS + " ORDER BY id");
                 ResultSet rs = ps.executeQuery()) {
                List<Produto> produtos = new ArrayList<>();
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
                return produtos;
            }
        });
    }

    public Optional<Produto> buscarPorId(long id) {
        return buscar(id, COLUNAS + " WHERE id = ?");
    }

    /**
     * Busca travando a linha (SELECT ... FOR UPDATE) ate o fim da transacao.
     * Evita que dois pedidos simultaneos leiam estoque = 1 e ambos vendam a ultima unidade.
     */
    public Optional<Produto> buscarPorIdParaAtualizacao(long id) {
        return buscar(id, COLUNAS + " WHERE id = ? FOR UPDATE");
    }

    public Produto inserir(Produto produto) {
        return comConexao("inserir produto", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO produto (nome, preco, estoque) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, produto.getNome());
                ps.setBigDecimal(2, produto.getPreco());
                ps.setInt(3, produto.getEstoque());
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    chaves.next();
                    produto.setId(chaves.getLong("id"));
                }
                return produto;
            }
        });
    }

    public void atualizar(Produto produto) {
        comConexao("atualizar produto", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE produto SET nome = ?, preco = ?, estoque = ? WHERE id = ?")) {
                ps.setString(1, produto.getNome());
                ps.setBigDecimal(2, produto.getPreco());
                ps.setInt(3, produto.getEstoque());
                ps.setLong(4, produto.getId());
                return ps.executeUpdate();
            }
        });
    }

    public void atualizarEstoque(long id, int estoque) {
        comConexao("atualizar estoque", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE produto SET estoque = ? WHERE id = ?")) {
                ps.setInt(1, estoque);
                ps.setLong(2, id);
                return ps.executeUpdate();
            }
        });
    }

    public void excluir(long id) {
        comConexao("excluir produto", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM produto WHERE id = ?")) {
                ps.setLong(1, id);
                return ps.executeUpdate();
            }
        });
    }

    /** Existe algum pedido (ativo ou cancelado) para o produto? */
    public boolean possuiHistorico(long id) {
        return comConexao("verificar historico do produto", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "SELECT EXISTS (SELECT 1 FROM pedido WHERE produto_id = ?)")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getBoolean(1);
                }
            }
        });
    }

    private Optional<Produto> buscar(long id, String sql) {
        return comConexao("buscar produto", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
                }
            }
        });
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(rs.getLong("id"), rs.getString("nome"), rs.getBigDecimal("preco"), rs.getInt("estoque"));
    }
}
