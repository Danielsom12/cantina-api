package br.com.cantina.service;

import br.com.cantina.dao.ClienteDao;
import br.com.cantina.dominio.Cliente;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public List<Cliente> listar() {
        return clienteDao.listar();
    }

    public Cliente buscar(long id) {
        return clienteDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }
}
