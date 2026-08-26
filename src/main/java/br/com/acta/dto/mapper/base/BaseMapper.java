package br.com.acta.dto.mapper.base;

import java.util.Collection;
import java.util.List;

public interface BaseMapper<REQ, RESP, ENT> {
    ENT toEntity(REQ dto);

    RESP toResponse(ENT entity);

    default List<RESP> toResponseList(Collection<ENT> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponse).toList();
    }
}
