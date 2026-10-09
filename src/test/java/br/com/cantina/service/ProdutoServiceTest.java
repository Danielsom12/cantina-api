package br.com.cantina.service;

import br.com.cantina.dao.ProdutoDao;
import br.com.cantina.dominio.Produto;
import br.com.cantina.dto.AtualizarProdutoRequest;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import br.com.cantina.excecao.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoDao produtoDao;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void comHistoricoPermiteAlterarNomeEPrecoMasNaoEstoque() {
        Produto coxinha = new Produto(10L, "Coxinha", new BigDecimal("7.50"), 5);
        when(produtoDao.buscarPorIdParaAtualizacao(10L)).thenReturn(Optional.of(coxinha));
        when(produtoDao.possuiHistorico(10L)).thenReturn(true);

        assertThatThrownBy(() -> produtoService.atualizar(10L,
                new AtualizarProdutoRequest("Coxinha", new BigDecimal("8.00"), 99)))
                .isInstanceOf(RegraNegocioException.class);
        verify(produtoDao, never()).atualizar(any());

        Produto atualizado = produtoService.atualizar(10L,
                new AtualizarProdutoRequest("Coxinha grande", new BigDecimal("8.00"), null));
        assertThat(atualizado.getNome()).isEqualTo("Coxinha grande");
        assertThat(atualizado.getEstoque()).isEqualTo(5);
    }

    @Test
    void naoExcluiProdutoComHistorico() {
        when(produtoDao.buscarPorIdParaAtualizacao(10L))
                .thenReturn(Optional.of(new Produto(10L, "Coxinha", BigDecimal.TEN, 1)));
        when(produtoDao.possuiHistorico(10L)).thenReturn(true);

        assertThatThrownBy(() -> produtoService.excluir(10L)).isInstanceOf(RegraNegocioException.class);
        verify(produtoDao, never()).excluir(anyLong());
    }

    @Test
    void excluirProdutoInexistenteRetornaNaoEncontrado() {
        when(produtoDao.buscarPorIdParaAtualizacao(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.excluir(404L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
