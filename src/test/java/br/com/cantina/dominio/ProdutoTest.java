package br.com.cantina.dominio;

import br.com.cantina.excecao.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProdutoTest {

    @Test
    void baixarUltimaUnidadeDeixaEstoqueZeradoEDepoisRejeita() {
        Produto produto = new Produto(1L, "Coxinha", new BigDecimal("7.50"), 1);

        produto.baixarUmaUnidade();

        assertThat(produto.getEstoque()).isZero();
        assertThatThrownBy(produto::baixarUmaUnidade).isInstanceOf(RegraNegocioException.class);
        assertThat(produto.getEstoque()).isZero(); // fronteira: nunca negativo
    }

    @Test
    void naoPermiteCriarProdutoComEstoqueNegativo() {
        assertThatThrownBy(() -> new Produto(1L, "Coxinha", BigDecimal.TEN, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
