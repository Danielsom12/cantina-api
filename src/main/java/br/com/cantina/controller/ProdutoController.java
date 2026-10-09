package br.com.cantina.controller;

import br.com.cantina.dominio.Produto;
import br.com.cantina.dto.AtualizarProdutoRequest;
import br.com.cantina.dto.CriarProdutoRequest;
import br.com.cantina.dto.ProdutoResponse;
import br.com.cantina.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(@Valid @RequestBody CriarProdutoRequest dados) {
        Produto produto = produtoService.cadastrar(dados);
        return ResponseEntity.created(URI.create("/produtos/" + produto.getId()))
                .body(ProdutoResponse.de(produto));
    }

    @GetMapping
    public List<ProdutoResponse> listar() {
        return produtoService.listar().stream().map(ProdutoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable long id) {
        return ProdutoResponse.de(produtoService.buscar(id));
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable long id, @Valid @RequestBody AtualizarProdutoRequest dados) {
        return ProdutoResponse.de(produtoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
