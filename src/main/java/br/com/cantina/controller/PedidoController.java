package br.com.cantina.controller;

import br.com.cantina.dominio.Pedido;
import br.com.cantina.dto.CriarPedidoRequest;
import br.com.cantina.dto.PedidoResponse;
import br.com.cantina.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> registrar(@Valid @RequestBody CriarPedidoRequest dados) {
        Pedido pedido = pedidoService.registrar(dados);
        return ResponseEntity.created(URI.create("/pedidos/" + pedido.getId()))
                .body(PedidoResponse.de(pedido));
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return pedidoService.listar().stream().map(PedidoResponse::de).toList();
    }

    @PatchMapping("/{id}/cancelamento")
    public PedidoResponse cancelar(@PathVariable long id) {
        return PedidoResponse.de(pedidoService.cancelar(id));
    }
}
