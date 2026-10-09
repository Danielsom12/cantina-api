package br.com.cantina.controller;

import br.com.cantina.dto.ErroResponse;
import br.com.cantina.excecao.PersistenciaException;
import br.com.cantina.excecao.RecursoNaoEncontradoException;
import br.com.cantina.excecao.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Converte excecoes em respostas HTTP coerentes:
 * 400 dados invalidos | 404 referencia inexistente | 409 regra/duplicidade/estado impossivel | 503 banco indisponivel.
 */
@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> dadosInvalidos(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<String> detalhes = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Dados invalidos", req, detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoIlegivel(HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisicao ausente ou em formato invalido", req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> parametroInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Parametro '" + e.getName() + "' invalido", req, null);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage(), req, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException e, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, e.getMessage(), req, null);
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<ErroResponse> persistencia(PersistenciaException e, HttpServletRequest req) {
        if (e.isFalhaDeConexao()) {
            return indisponivel(e, req);
        }
        log.error("Erro de persistencia", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao acessar os dados", req, null);
    }

    /** Banco fora do ar ao abrir conexao/transacao. */
    @ExceptionHandler({DataAccessResourceFailureException.class, CannotCreateTransactionException.class})
    public ResponseEntity<ErroResponse> indisponivel(Exception e, HttpServletRequest req) {
        log.warn("Banco de dados indisponivel: {}", e.getMessage());
        return responder(HttpStatus.SERVICE_UNAVAILABLE, "Banco de dados indisponivel. Tente novamente.", req, null);
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem, HttpServletRequest req,
                                                   List<String> detalhes) {
        ErroResponse corpo = new ErroResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, req.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
