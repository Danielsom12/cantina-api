package br.com.cantina.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Um pedido = um produto e uma unidade; por isso nao ha campo de quantidade. */
public record CriarPedidoRequest(
        @NotNull(message = "clienteId e obrigatorio")
        @Positive(message = "clienteId deve ser positivo")
        Long clienteId,

        @NotNull(message = "produtoId e obrigatorio")
        @Positive(message = "produtoId deve ser positivo")
        Long produtoId) {
}
