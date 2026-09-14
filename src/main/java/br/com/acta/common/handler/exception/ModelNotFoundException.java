package br.com.acta.common.handler.exception;

import java.util.UUID;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String recurso, UUID id) {
        super(recurso + "não foi encontrado com o ID: " + id);
    }
}
