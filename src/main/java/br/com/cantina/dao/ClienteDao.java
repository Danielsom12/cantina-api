package br.com.cantina.dao;

import br.com.cantina.dominio.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Clientes sao somente consultados nesta AVC. */
@Repository
public class ClienteDao extends JdbcDao {

    public ClienteDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Cliente> listar() {
        return comConexao("listar clientes", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "SELECT id, nome, email FROM cliente ORDER BY id");
                 ResultSet rs = ps.executeQuery()) {
                List<Cliente> clientes = new ArrayList<>();
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
                return clientes;
            }
        });
    }

    public Optional<Cliente> buscarPorId(long id) {
        return comConexao("buscar cliente", conexao -> {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "SELECT id, nome, email FROM cliente WHERE id = ?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
                }
            }
        });
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getLong("id"), rs.getString("nome"), rs.getString("email"));
    }
}
