package br.com.acta.common.handler;

import br.com.acta.common.handler.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarBadRequestParaFalhaDoConversor() {
        ResponseEntity<ErroResponse> resposta = handler.handleInvalidRequest(
                new InvalidRequestException("Lista inválida"));

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("Lista inválida", resposta.getBody().mensagens().get(0));
    }

    @Test
    void deveRetornarBadRequestParaTipoIncompativelNoPatch() {
        ResponseEntity<ErroResponse> resposta = handler.handleInvalidRequest(
                new ClassCastException());

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("O corpo da requisição possui um valor com tipo inválido",
                resposta.getBody().mensagens().get(0));
    }
}
