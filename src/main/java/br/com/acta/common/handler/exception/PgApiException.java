package br.com.acta.common.handler.exception;

public class PgApiException extends RuntimeException {
    public PgApiException() {
        super("A API PostgreSQL retornou uma resposta vazia");
    }

    public PgApiException(String mensagem){
        super(mensagem);
    }
}
