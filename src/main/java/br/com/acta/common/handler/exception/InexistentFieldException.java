package br.com.acta.common.handler.exception;

public class InexistentFieldException extends RuntimeException {
    public InexistentFieldException(String campo) {
        super("Campo " + campo + " inexistente");
    }
}
