package br.com.acta.common.handler.exception;

public class DuplicateFormResponseException extends RuntimeException {
    public DuplicateFormResponseException() {
        super("O usuário autenticado já respondeu a este formulário");
    }
}