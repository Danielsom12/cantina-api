package br.com.cantina.service;

import br.com.cantina.dao.ClienteDao;
import br.com.cantina.dao.PedidoDao;
import br.com.cantina.dao.ProdutoDao;
import br.com.cantina.dominio.Cliente;
import br.com.cantina.dominio.Pedido;
import br.com.cantina.dominio.Produto;
import br.com.cantina.dominio.SituacaoPedido;
import br.com.cantina.dto.CriarPedidoRequest;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import br.com.cantina.excecao.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Testes de regra de negocio do PedidoService, com os DAOs simulados (sem banco). */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoDao pedidoDao;
    @Mock
    private ClienteDao clienteDao;
    @Mock
    private ProdutoDao produtoDao;

    @InjectMocks
    private PedidoService pedidoService;

    private final Cliente ana = new Cliente(1L, "Ana", "ana@email.com");

    @Test
    void pedirProdutoComEstoqueRegistraPrecoEBaixaUmaUnidade() {
        Produto coxinha = new Produto(10L, "Coxinha", new BigDecimal("7.50"), 5);
        when(clienteDao.buscarPorId(1L)).thenReturn(Optional.of(ana));
        when(produtoDao.buscarPorIdParaAtualizacao(10L)).thenReturn(Optional.of(coxinha));
        when(pedidoDao.inserir(any())).thenAnswer(inv -> inv.getArgument(0));

        Pedido pedido = pedidoService.registrar(new CriarPedidoRequest(1L, 10L));

        assertThat(pedido.getPrecoRegistrado()).isEqualByComparingTo("7.50");
        assertThat(pedido.getSituacao()).isEqualTo(SituacaoPedido.ATIVO);
        verify(produtoDao).atualizarEstoque(10L, 4);
    }

    @Test
    void pedirProdutoSemEstoqueRejeitaSemRegistrarPedido() {
        Produto bolo = new Produto(20L, "Bolo", new BigDecimal("6.00"), 0);
        when(clienteDao.buscarPorId(1L)).thenReturn(Optional.of(ana));
        when(produtoDao.buscarPorIdParaAtualizacao(20L)).thenReturn(Optional.of(bolo));

        assertThatThrownBy(() -> pedidoService.registrar(new CriarPedidoRequest(1L, 20L)))
                .isInstanceOf(RegraNegocioException.class);

        verify(pedidoDao, never()).inserir(any());
        verify(produtoDao, never()).atualizarEstoque(anyLong(), anyInt());
    }

    @Test
    void clienteInexistenteRejeitaSemAlterarEstoque() {
        when(clienteDao.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.registrar(new CriarPedidoRequest(999L, 10L)))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(produtoDao, never()).atualizarEstoque(anyLong(), anyInt());
        verify(pedidoDao, never()).inserir(any());
    }

    @Test
    void alterarPrecoDoProdutoNaoMudaPrecoDoPedidoJaCriado() {
        Produto coxinha = new Produto(10L, "Coxinha", new BigDecimal("7.50"), 5);
        Pedido pedido = Pedido.novo(ana, coxinha);

        coxinha.atualizarCadastro("Coxinha", new BigDecimal("9.90"));

        assertThat(pedido.getPrecoRegistrado()).isEqualByComparingTo("7.50");
    }

    @Test
    void cancelarRepoeUmaUnidadeApenasNaPrimeiraChamada() {
        Produto coxinha = new Produto(10L, "Coxinha", new BigDecimal("7.50"), 4);
        Pedido ativo = new Pedido(100L, ana, coxinha, new BigDecimal("7.50"), LocalDateTime.now(), SituacaoPedido.ATIVO);
        when(pedidoDao.buscarPorIdParaAtualizacao(100L)).thenReturn(Optional.of(ativo));
        when(produtoDao.buscarPorIdParaAtualizacao(10L)).thenReturn(Optional.of(coxinha));

        Pedido cancelado = pedidoService.cancelar(100L);

        assertThat(cancelado.getSituacao()).isEqualTo(SituacaoPedido.CANCELADO);
        verify(produtoDao).atualizarEstoque(10L, 5);

        // segunda chamada: o banco agora devolve o pedido ja cancelado
        assertThatThrownBy(() -> pedidoService.cancelar(100L)).isInstanceOf(RegraNegocioException.class);
        verify(produtoDao, times(1)).atualizarEstoque(anyLong(), anyInt());
    }
}
