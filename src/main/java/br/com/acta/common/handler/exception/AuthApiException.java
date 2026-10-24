package br.com.acta.common.handler.exception;

public class AuthApiException extends RuntimeException {
    public AuthApiException() {
        super("A API de autenticação retornou uma resposta vazia");
    }

    public AuthApiException(String mensagem) {
        super(mensagem);
    }
}