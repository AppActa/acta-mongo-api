package br.com.acta.common.handler.exception;

public class ImmutableFieldException extends RuntimeException {
    public ImmutableFieldException(String campo) {
        super("Campo " + campo + " não pode ser alterado");
    }
}
