package br.com.cantina.algoritmos;

import br.com.cantina.dominio.Produto;

import java.util.List;

/**
 * Buscas por ID sobre uma colecao EM MEMORIA de produtos (independente das rotas da API).
 * Retornam o indice do produto na lista, ou -1 se nao encontrar.
 */
public final class BuscaProduto {

    private BuscaProduto() {
    }

    /** Busca linear: percorre um a um. Funciona em lista desordenada. O(n). */
    public static int linearPorId(List<Produto> produtos, long id) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Busca binaria: a cada passo descarta metade da lista. O(log n).
     * PRE-CONDICAO: a lista precisa estar ordenada pelo MESMO criterio da busca (ID crescente).
     * Se estiver ordenada por preco, por exemplo, "ir para a esquerda/direita" deixa de fazer sentido
     * e o algoritmo pode nao achar um ID que existe.
     */
    public static int binariaPorId(List<Produto> produtosOrdenadosPorId, long id) {
        int inicio = 0;
        int fim = produtosOrdenadosPorId.size() - 1;
        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2; // evita overflow de (inicio + fim)
            long idMeio = produtosOrdenadosPorId.get(meio).getId();
            if (idMeio == id) {
                return meio;
            } else if (idMeio < id) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return -1;
    }
}
