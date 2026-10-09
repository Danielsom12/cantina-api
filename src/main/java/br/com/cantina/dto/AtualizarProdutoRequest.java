package br.com.cantina.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados permitidos no PUT: nome e preco. O estoque e opcional e so pode ser alterado
 * enquanto o produto nao tiver historico de pedidos (regra aplicada no ProdutoService).
 */
public record AtualizarProdutoRequest(
        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 120, message = "nome deve ter no maximo 120 caracteres")
        String nome,

        @NotNull(message = "preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "preco deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "preco deve ter no maximo 2 casas decimais")
        BigDecimal preco,

        @PositiveOrZero(message = "estoque nao pode ser negativo")
        Integer estoque) {
}
