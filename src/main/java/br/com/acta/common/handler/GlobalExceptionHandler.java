package br.com.acta.common.handler;

import br.com.acta.common.handler.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ViaCepException.class)
    public ResponseEntity<ErroResponse> handleViaCepException(ViaCepException vce) {
        log.error("mensagem", vce);
        return erro(HttpStatus.BAD_GATEWAY, List.of(vce.getMessage()));
    }

    @ExceptionHandler(PgApiException.class)
    public ResponseEntity<ErroResponse> handlePgApiException(PgApiException pae) {
        log.error("mensagem", pae);
        return erro(HttpStatus.BAD_GATEWAY, List.of(pae.getMessage()));
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErroResponse> handleModelNotFound(DocumentNotFoundException dnfe) {
        log.error("mensagem", dnfe);
        return erro(HttpStatus.NOT_FOUND, List.of(dnfe.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(AccessDeniedException ade) {
        log.error("mensagem", ade);
        return erro(HttpStatus.FORBIDDEN, List.of(ade.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException hrmnse) {
        log.error("mensagem", hrmnse);
        return erro(HttpStatus.METHOD_NOT_ALLOWED, List.of("Método HTTP não permitido para este recurso"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException hmtnse) {
        log.error("mensagem", hmtnse);
        return erro(HttpStatus.UNSUPPORTED_MEDIA_TYPE, List.of("Tipo de conteúdo não suportado"));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErroResponse> handleDuplicateKey(DuplicateKeyException dke) {
        log.error("mensagem", dke);
        return erro(HttpStatus.CONFLICT, List.of("Já existe um registro com os dados informados"));
    }

    @ExceptionHandler(DuplicateFormResponseException.class)
    public ResponseEntity<ErroResponse> handleDuplicateFormResponse(DuplicateFormResponseException dfre) {
        log.error("mensagem", dfre);
        return erro(HttpStatus.CONFLICT, List.of(dfre.getMessage()));
    }

    @ExceptionHandler({ImmutableFieldException.class, InexistentFieldException.class})
    public ResponseEntity<ErroResponse> handleInvalidPatchField(RuntimeException re) {
        log.error("mensagem", re);
        return erro(HttpStatus.BAD_REQUEST, List.of(re.getMessage()));
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErroResponse> handleInvalidRequest(ClassCastException cce) {
        log.error("mensagem", cce);
        return erro(HttpStatus.BAD_REQUEST, List.of("O corpo da requisição possui um valor com tipo inválido"));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErroResponse> handleInvalidRequest(InvalidRequestException ire) {
        log.error("mensagem", ire);
        return erro(HttpStatus.BAD_REQUEST, List.of(ire.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException manve) {
        log.error("mensagem", manve);
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
    public ResponseEntity<ErroResponse> handleInvalidRequest(Exception e) {
        log.error("mensagem", e);
        return erro(HttpStatus.BAD_REQUEST, List.of("A requisição informada é inválida"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> handleNoResourceFound(NoResourceFoundException nrfe) {
        log.error("mensagem", nrfe);
        return erro(HttpStatus.NOT_FOUND, List.of("O recurso solicitado não foi encontrado"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException(Exception e) {
        log.error("mensagem", e);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Ocorreu um erro interno inesperado"));
    }

    private ResponseEntity<ErroResponse> erro(HttpStatus status, List<String> mensagens) {
        return ResponseEntity.status(status).body(new ErroResponse(mensagens, status.value()));
    }
}