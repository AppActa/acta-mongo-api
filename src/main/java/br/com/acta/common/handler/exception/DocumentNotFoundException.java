package br.com.acta.common.handler.exception;

import java.util.UUID;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String recurso, UUID id) {
        super(recurso + "não foi encontrado com o ID: " + id);
    }
}
