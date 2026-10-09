package br.com.cantina.service;

import br.com.cantina.dao.ClienteDao;
import br.com.cantina.dao.PedidoDao;
import br.com.cantina.dao.ProdutoDao;
import br.com.cantina.dominio.Cliente;
import br.com.cantina.dominio.Pedido;
import br.com.cantina.dominio.Produto;
import br.com.cantina.dto.CriarPedidoRequest;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Operacao principal do tema (registrar pedido) e sua acao inversa (cancelar).
 * Cada uma roda em UMA transacao: pedido e estoque mudam juntos ou nao mudam.
 */
@Service
public class PedidoService {

    private final PedidoDao pedidoDao;
    private final ClienteDao clienteDao;
    private final ProdutoDao produtoDao;

    public PedidoService(PedidoDao pedidoDao, ClienteDao clienteDao, ProdutoDao produtoDao) {
        this.pedidoDao = pedidoDao;
        this.clienteDao = clienteDao;
        this.produtoDao = produtoDao;
    }

    public List<Pedido> listar() {
        return pedidoDao.listar();
    }

    @Transactional
    public Pedido registrar(CriarPedidoRequest dados) {
        Cliente cliente = clienteDao.buscarPorId(dados.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", dados.clienteId()));
        Produto produto = produtoDao.buscarPorIdParaAtualizacao(dados.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", dados.produtoId()));

        produto.baixarUmaUnidade();                 // rejeita se estoque = 0
        Pedido pedido = Pedido.novo(cliente, produto); // copia o preco atual

        produtoDao.atualizarEstoque(produto.getId(), produto.getEstoque());
        return pedidoDao.inserir(pedido);
    }

    @Transactional
    public Pedido cancelar(long id) {
        Pedido pedido = pedidoDao.buscarPorIdParaAtualizacao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        pedido.cancelar();                          // rejeita se ja estiver cancelado

        long produtoId = pedido.getProduto().getId();
        Produto produto = produtoDao.buscarPorIdParaAtualizacao(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", produtoId));
        produto.reporUmaUnidade();

        pedidoDao.atualizarSituacao(id, pedido.getSituacao());
        produtoDao.atualizarEstoque(produtoId, produto.getEstoque());
        return pedido;
    }
}
