package br.com.cantina.algoritmos;

import br.com.cantina.dominio.Produto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Merge Sort aplicado a produtos. Criterio relevante do tema: ordenar o cardapio por preco
 * (e por ID, para preparar a busca binaria).
 * <p>
 * Complexidade: O(n log n) em todos os casos; memoria extra O(n). E estavel: produtos com o mesmo
 * preco mantem a ordem original.
 */
public final class OrdenacaoProduto {

    public static final Comparator<Produto> POR_PRECO = Comparator.comparing(Produto::getPreco);
    public static final Comparator<Produto> POR_ID = Comparator.comparing(Produto::getId);

    private OrdenacaoProduto() {
    }

    /** Devolve uma NOVA lista ordenada; a lista original nao e modificada. */
    public static List<Produto> mergeSort(List<Produto> produtos, Comparator<Produto> criterio) {
        if (produtos.size() <= 1) {
            return new ArrayList<>(produtos);
        }
        int meio = produtos.size() / 2;
        List<Produto> esquerda = mergeSort(produtos.subList(0, meio), criterio);
        List<Produto> direita = mergeSort(produtos.subList(meio, produtos.size()), criterio);
        return intercalar(esquerda, direita, criterio);
    }

    private static List<Produto> intercalar(List<Produto> esquerda, List<Produto> direita, Comparator<Produto> criterio) {
        List<Produto> resultado = new ArrayList<>(esquerda.size() + direita.size());
        int i = 0;
        int j = 0;
        while (i < esquerda.size() && j < direita.size()) {
            // "<=" mantem a estabilidade: em empate, vem primeiro o da esquerda
            if (criterio.compare(esquerda.get(i), direita.get(j)) <= 0) {
                resultado.add(esquerda.get(i++));
            } else {
                resultado.add(direita.get(j++));
            }
        }
        while (i < esquerda.size()) {
            resultado.add(esquerda.get(i++));
        }
        while (j < direita.size()) {
            resultado.add(direita.get(j++));
        }
        return resultado;
    }
}
