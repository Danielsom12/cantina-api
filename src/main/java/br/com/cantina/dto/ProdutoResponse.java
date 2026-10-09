package br.com.cantina.dto;

import br.com.cantina.dominio.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, BigDecimal preco, int estoque) {

    public static ProdutoResponse de(Produto produto) {
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getPreco(), produto.getEstoque());
    }
}
