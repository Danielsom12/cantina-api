package br.com.cantina.dominio;

import java.util.Objects;

/**
 * Cliente da cantina. Nesta AVC os clientes vem prontos do banco e sao somente consultados,
 * por isso a classe e imutavel (sem setters).
 */
public final class Cliente {

    private final Long id;
    private final String nome;
    private final String email;

    public Cliente(Long id, String nome, String email) {
        this.id = Objects.requireNonNull(id, "id");
        this.nome = Objects.requireNonNull(nome, "nome");
        this.email = Objects.requireNonNull(email, "email");
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
