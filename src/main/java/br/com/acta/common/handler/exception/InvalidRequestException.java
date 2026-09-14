package br.com.acta.common.handler.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String mensagem) {
        super(mensagem);
    }
}
