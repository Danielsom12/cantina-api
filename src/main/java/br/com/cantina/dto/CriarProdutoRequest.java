package br.com.cantina.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarProdutoRequest(
        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 120, message = "nome deve ter no maximo 120 caracteres")
        String nome,

        @NotNull(message = "preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "preco deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "preco deve ter no maximo 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "estoque e obrigatorio")
        @PositiveOrZero(message = "estoque nao pode ser negativo")
        Integer estoque) {
}
