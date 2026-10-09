package br.com.cantina.dominio;

import br.com.cantina.excecao.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Pedido de UM produto e UMA unidade (nao ha carrinho nem tabela de itens).
 * O preco e copiado do produto no momento da criacao e nunca mais muda.
 */
public class Pedido {

    private Long id;
    private final Cliente cliente;
    private final Produto produto;
    private final BigDecimal precoRegistrado;
    private LocalDateTime dataPedido;
    private SituacaoPedido situacao;

    public Pedido(Long id, Cliente cliente, Produto produto, BigDecimal precoRegistrado,
                  LocalDateTime dataPedido, SituacaoPedido situacao) {
        this.id = id;
        this.cliente = Objects.requireNonNull(cliente, "cliente");
        this.produto = Objects.requireNonNull(produto, "produto");
        this.precoRegistrado = Objects.requireNonNull(precoRegistrado, "precoRegistrado");
        this.dataPedido = dataPedido;
        this.situacao = Objects.requireNonNull(situacao, "situacao");
    }

    /** Cria um novo pedido ativo copiando o preco atual do produto. */
    public static Pedido novo(Cliente cliente, Produto produto) {
        return new Pedido(null, cliente, produto, produto.getPreco(), null, SituacaoPedido.ATIVO);
    }

    public boolean isAtivo() {
        return situacao == SituacaoPedido.ATIVO;
    }

    /** Um pedido cancelado nao pode ser cancelado novamente. */
    public void cancelar() {
        if (!isAtivo()) {
            throw new RegraNegocioException("Pedido " + id + " ja esta cancelado");
        }
        situacao = SituacaoPedido.CANCELADO;
    }

    /** Preenchido pelo DAO depois do INSERT (id e data gerados pelo banco). */
    public void registrarPersistencia(Long id, LocalDateTime dataPedido) {
        this.id = id;
        this.dataPedido = dataPedido;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public BigDecimal getPrecoRegistrado() {
        return precoRegistrado;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public SituacaoPedido getSituacao() {
        return situacao;
    }
}
