package br.com.cantina.dominio;

import br.com.cantina.excecao.RegraNegocioException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Produto vendido pela cantina. O estoque so muda pelos metodos de negocio
 * ({@link #baixarUmaUnidade()} e {@link #reporUmaUnidade()}), nunca por setter publico.
 */
public class Produto {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private int estoque;

    public Produto(Long id, String nome, BigDecimal preco, int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("Estoque nao pode ser negativo");
        }
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "nome");
        this.preco = Objects.requireNonNull(preco, "preco");
        this.estoque = estoque;
    }

    public boolean temEstoque() {
        return estoque > 0;
    }

    /** Consome uma unidade para um novo pedido. Garante que o estoque nunca fica negativo. */
    public void baixarUmaUnidade() {
        if (!temEstoque()) {
            throw new RegraNegocioException("Produto " + id + " sem estoque disponivel");
        }
        estoque--;
    }

    /** Devolve uma unidade quando um pedido e cancelado. */
    public void reporUmaUnidade() {
        estoque++;
    }

    /** Atualizacao cadastral: nome e preco. Nao afeta pedidos ja registrados (eles guardam o proprio preco). */
    public void atualizarCadastro(String novoNome, BigDecimal novoPreco) {
        this.nome = Objects.requireNonNull(novoNome, "nome");
        this.preco = Objects.requireNonNull(novoPreco, "preco");
    }

    /** Ajuste direto de estoque: o Service so permite quando o produto ainda nao tem historico. */
    public void definirEstoqueInicial(int novoEstoque) {
        if (novoEstoque < 0) {
            throw new IllegalArgumentException("Estoque nao pode ser negativo");
        }
        this.estoque = novoEstoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', preco=" + preco + ", estoque=" + estoque + '}';
    }
}
