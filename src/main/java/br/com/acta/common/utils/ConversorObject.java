package br.com.acta.common.utils;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.embedded.Porque;
import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.document.enums.TipoFormulario;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ConversorObject {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ConversorObject() {}

    public static List<CausaIshikawa> toCausasIshikawa(Object valor) {
        if (!(valor instanceof List<?> lista))
            throw new InvalidRequestException("O campo causas deve ser uma lista");

        try {
            return lista.stream()
                    .map(item -> OBJECT_MAPPER.convertValue(item, CausaIshikawa.class))
                    .toList();
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("Uma das causas informadas é inválida");
        }
    }

    public static List<Porque> toPorques(Object valor) {
        if (!(valor instanceof List<?> lista))
            throw new InvalidRequestException("O campo porques deve ser uma lista");

        try {
            return lista.stream()
                    .map(item -> OBJECT_MAPPER.convertValue(item, Porque.class))
                    .toList();
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("Um dos porquês informados é inválido");
        }
    }

    public static List<Pergunta> toPerguntas(Object valor) {
        return converterLista(valor, Pergunta.class, "perguntas", "Uma das perguntas informadas é inválida");
    }

    public static List<Long> toLongList(Object valor, String campo) {
        return converterLista(valor, Long.class, campo, "Um dos valores informados no campo " + campo + " é inválido");
    }

    public static TipoFormulario toTipoFormulario(Object valor) {
        return converter(valor, TipoFormulario.class, "tipo");
    }

    public static StatusFormulario toStatusFormulario(Object valor) {
        return converter(valor, StatusFormulario.class, "status");
    }

    public static UUID toUUID(Object valor, String campo) {
        return converter(valor, UUID.class, campo);
    }

    public static Instant toInstant(Object valor, String campo) {
        if (valor instanceof Instant instant) return instant;

        try {
            return Instant.parse(valor.toString());
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("O campo " + campo + " possui um valor inválido");
        }
    }

    public static List<RespostaPerguntaRequestDTO> toRespostasPergunta(Object valor) {
        return converterLista(
                valor,
                RespostaPerguntaRequestDTO.class,
                "respostas",
                "Uma das respostas informadas é inválida"
        );
    }

    private static <T> T converter(Object valor, Class<T> tipo, String campo) {
        try {
            return OBJECT_MAPPER.convertValue(valor, tipo);
        } catch (RuntimeException exception) {
            throw new InvalidRequestException("O campo " + campo + " possui um valor inválido");
        }
    }

    private static <T> List<T> converterLista(Object valor, Class<T> tipo, String campo, String mensagemItemInvalido) {
        if (!(valor instanceof List<?> lista))
            throw new InvalidRequestException("O campo " + campo + " deve ser uma lista");

        try {
            return lista.stream().map(item -> OBJECT_MAPPER.convertValue(item, tipo)).toList();
        } catch (RuntimeException exception) {
            throw new InvalidRequestException(mensagemItemInvalido);
        }
    }
}
