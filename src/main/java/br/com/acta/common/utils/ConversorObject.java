package br.com.acta.common.utils;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.document.embedded.Porque;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class ConversorObject {
    private final ObjectMapper objectMapper;

    public List<CausaIshikawa> toCausasIshikawa(Object valor) {
        if (!(valor instanceof List<?> lista))
            throw new InvalidRequestException("O campo causas deve ser uma lista");

        try {
            return lista.stream()
                    .map(item -> objectMapper.convertValue(item, CausaIshikawa.class))
                    .toList();
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("Uma das causas informadas é inválida");
        }
    }

    public List<Porque> toPorques(Object valor) {
        if (!(valor instanceof List<?> lista))
            throw new InvalidRequestException("O campo porques deve ser uma lista");

        try {
            return lista.stream()
                    .map(item -> objectMapper.convertValue(item, Porque.class))
                    .toList();
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("Um dos porquês informados é inválido");
        }
    }
}
