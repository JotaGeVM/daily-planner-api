package io.github.jotagevm.daily_planner_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CategoriaJaCadastrada.class)
    public ResponseEntity<ErroResposta> handleCategoriaJaCadastradaException(CategoriaJaCadastrada ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT.value())
                .body(new ErroResposta(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(EmailJaCadastrado.class)
    public ResponseEntity<ErroResposta> handleEmailJaCadastrado(EmailJaCadastrado ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT.value())
                .body(new ErroResposta(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(CredenciaisInvalidas.class)
    public ResponseEntity<ErroResposta> handleCredenciaisInvalidas(CredenciaisInvalidas ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(new ErroResposta(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(CategoriaNaoEncontrada.class)
    public ResponseEntity<ErroResposta> handleCategoriaNaoEncontrada(CategoriaNaoEncontrada ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(new ErroResposta(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DiasSemanaObrigatorio.class)
    public ResponseEntity<ErroResposta> handleDiasSemanaObrigatorio(DiasSemanaObrigatorio ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(new ErroResposta(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HorarioObrigatorio.class)
    public ResponseEntity<ErroResposta> handleHorarioObrigatorio(HorarioObrigatorio ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(new ErroResposta(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(OcorrenciaNaoEncontrada.class)
    public ResponseEntity<ErroResposta> handleOcorrenciaNaoEncontrada(OcorrenciaNaoEncontrada ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(new ErroResposta(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(TarefaNaoEncontrada.class)
    public ResponseEntity<ErroResposta> handleTarefaNaoEncontrada(TarefaNaoEncontrada ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(new ErroResposta(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(CategoriaEmUso.class)
    public ResponseEntity<ErroResposta> handleCategoriaEmUso(CategoriaEmUso ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT.value())
                .body(new ErroResposta(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> handleMensagemIlegivel(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResposta(HttpStatus.BAD_REQUEST.value(), "Corpo da requisição inválido ou malformado",
                        LocalDateTime.now()));
    }
}
