package br.com.acta.service.base;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseCRUD<REQ, RESP> {
    RESP buscar(UUID id);
    List<RESP> buscar();
    RESP inserir(REQ dto);
    RESP patch(UUID id, Map<String, Object> campos);
    void excluir(UUID id);
}
