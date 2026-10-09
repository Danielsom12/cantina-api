package br.com.cantina.algoritmos;

import br.com.cantina.dominio.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Gera uma colecao de produtos de forma simples e REPRODUZIVEL (mesma semente = mesma lista). */
public final class GeradorProdutos {

    private static final String[] NOMES = {"Coxinha", "Pastel", "Suco", "Cafe", "Bolo", "Pao de queijo", "Esfiha"};

    private GeradorProdutos() {
    }

    /** IDs de 1..quantidade, embaralhados, com precos entre 2,00 e 20,00. */
    public static List<Produto> gerar(int quantidade, long semente) {
        Random random = new Random(semente);
        List<Produto> produtos = new ArrayList<>(quantidade);
        for (long id = 1; id <= quantidade; id++) {
            BigDecimal preco = BigDecimal.valueOf(2 + random.nextDouble() * 18).setScale(2, RoundingMode.HALF_UP);
            String nome = NOMES[random.nextInt(NOMES.length)] + " " + id;
            produtos.add(new Produto(id, nome, preco, random.nextInt(50)));
        }
        Collections.shuffle(produtos, random);
        return produtos;
    }
}
