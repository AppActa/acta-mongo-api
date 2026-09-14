package br.com.acta.common.handler;

import br.com.acta.common.handler.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PgApiException.class)
    public ResponseEntity<ErroResponse> handlePgApiException(PgApiException pae) {
        return erro(HttpStatus.BAD_GATEWAY, List.of(pae.getMessage()));
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErroResponse> handleModelNotFound(DocumentNotFoundException dnfe) {
        return erro(HttpStatus.NOT_FOUND, List.of(dnfe.getMessage()));
    }

    @ExceptionHandler({ImmutableFieldException.class, InexistentFieldException.class})
    public ResponseEntity<ErroResponse> handleInvalidPatchField(RuntimeException exception) {
        return erro(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException manve) {
        List<String> mensagens = manve.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        return erro(HttpStatus.BAD_REQUEST, mensagens);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErroResponse> handleInvalidRequest() {
        return erro(HttpStatus.BAD_REQUEST, List.of("A requisição informada é inválida"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> handleNoResourceFound() {
        return erro(HttpStatus.NOT_FOUND, List.of("O recurso solicitado não foi encontrado"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException() {
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Ocorreu um erro interno inesperado"));
    }

    private ResponseEntity<ErroResponse> erro(HttpStatus status, List<String> mensagens) {
        return ResponseEntity.status(status).body(new ErroResponse(mensagens, status.value()));
    }
}
