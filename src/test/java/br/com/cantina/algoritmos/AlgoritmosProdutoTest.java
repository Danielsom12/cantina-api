package br.com.cantina.algoritmos;

import br.com.cantina.dominio.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlgoritmosProdutoTest {

    private final List<Produto> produtos = GeradorProdutos.gerar(50, 42L);

    @Test
    void buscaLinearEBinariaEncontramOMesmoProduto() {
        List<Produto> ordenadosPorId = OrdenacaoProduto.mergeSort(produtos, OrdenacaoProduto.POR_ID);

        int idxLinear = BuscaProduto.linearPorId(produtos, 37);
        int idxBinaria = BuscaProduto.binariaPorId(ordenadosPorId, 37);

        assertThat(produtos.get(idxLinear).getId()).isEqualTo(37L);
        assertThat(ordenadosPorId.get(idxBinaria).getId()).isEqualTo(37L);
    }

    @Test
    void buscasRetornamMenosUmParaIdInexistente() {
        List<Produto> ordenadosPorId = OrdenacaoProduto.mergeSort(produtos, OrdenacaoProduto.POR_ID);

        assertThat(BuscaProduto.linearPorId(produtos, 999)).isEqualTo(-1);
        assertThat(BuscaProduto.binariaPorId(ordenadosPorId, 999)).isEqualTo(-1);
        assertThat(BuscaProduto.binariaPorId(List.of(), 1)).isEqualTo(-1);
    }

    @Test
    void mergeSortOrdenaPorPrecoSemAlterarListaOriginal() {
        Produto primeiroOriginal = produtos.get(0);

        List<Produto> ordenados = OrdenacaoProduto.mergeSort(produtos, OrdenacaoProduto.POR_PRECO);

        assertThat(ordenados).hasSameSizeAs(produtos).isSortedAccordingTo(OrdenacaoProduto.POR_PRECO);
        assertThat(produtos.get(0)).isSameAs(primeiroOriginal);
    }

    @Test
    void mergeSortEEstavelEmPrecosIguais() {
        Produto a = new Produto(3L, "A", new BigDecimal("5.00"), 1);
        Produto b = new Produto(1L, "B", new BigDecimal("5.00"), 1);
        Produto c = new Produto(2L, "C", new BigDecimal("2.00"), 1);

        List<Produto> ordenados = OrdenacaoProduto.mergeSort(List.of(a, b, c), OrdenacaoProduto.POR_PRECO);

        assertThat(ordenados).containsExactly(c, a, b);
    }
}
