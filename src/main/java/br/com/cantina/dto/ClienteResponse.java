package br.com.cantina.dto;

import br.com.cantina.dominio.Cliente;

public record ClienteResponse(Long id, String nome, String email) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}
