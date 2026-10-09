package br.com.cantina.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Formato unico de erro devolvido pela API. */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes) {
}
