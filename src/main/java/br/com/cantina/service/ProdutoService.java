package br.com.cantina.service;

import br.com.cantina.dao.ProdutoDao;
import br.com.cantina.dominio.Produto;
import br.com.cantina.dto.AtualizarProdutoRequest;
import br.com.cantina.dto.CriarProdutoRequest;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import br.com.cantina.excecao.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoDao produtoDao;

    public ProdutoService(ProdutoDao produtoDao) {
        this.produtoDao = produtoDao;
    }

    public List<Produto> listar() {
        return produtoDao.listar();
    }

    public Produto buscar(long id) {
        return produtoDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    public Produto cadastrar(CriarProdutoRequest dados) {
        Produto produto = new Produto(null, dados.nome().trim(), dados.preco(), dados.estoque());
        return produtoDao.inserir(produto);
    }

    /**
     * Atualizacao cadastral: nome e preco sempre. Depois que existe historico, o estoque so e
     * movimentado por pedidos/cancelamentos; tentar altera-lo pelo PUT e rejeitado.
     * Mudar o preco NAO altera pedidos antigos, pois cada pedido guarda o proprio preco_registrado.
     */
    @Transactional
    public Produto atualizar(long id, AtualizarProdutoRequest dados) {
        Produto produto = produtoDao.buscarPorIdParaAtualizacao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        produto.atualizarCadastro(dados.nome().trim(), dados.preco());

        Integer novoEstoque = dados.estoque();
        if (novoEstoque != null && novoEstoque != produto.getEstoque()) {
            if (produtoDao.possuiHistorico(id)) {
                throw new RegraNegocioException(
                        "Produto " + id + " possui pedidos: o estoque so muda por pedido ou cancelamento");
            }
            produto.definirEstoqueInicial(novoEstoque);
        }

        produtoDao.atualizar(produto);
        return produto;
    }

    /** Exclusao so e permitida sem historico, para nao destruir pedidos registrados. */
    @Transactional
    public void excluir(long id) {
        produtoDao.buscarPorIdParaAtualizacao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        if (produtoDao.possuiHistorico(id)) {
            throw new RegraNegocioException("Produto " + id + " possui historico de pedidos e nao pode ser excluido");
        }
        produtoDao.excluir(id);
    }
}
