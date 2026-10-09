package br.com.cantina.dto;

import br.com.cantina.dominio.Pedido;
import br.com.cantina.dominio.SituacaoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Long produtoId,
        String produtoNome,
        BigDecimal precoRegistrado,
        LocalDateTime dataPedido,
        SituacaoPedido situacao) {

    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getProduto().getId(),
                pedido.getProduto().getNome(),
                pedido.getPrecoRegistrado(),
                pedido.getDataPedido(),
                pedido.getSituacao());
    }
}
