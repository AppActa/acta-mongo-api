package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;

import java.util.List;

public interface ValidacaoRespostaStrategy {
    TipoResposta getTipo();

    void validar(Pergunta pergunta, Object resposta);

    default String texto(Object resposta, Pergunta pergunta, String formatoEsperado) {
        try {
            return (String) resposta;
        } catch (ClassCastException cce) {
            throw respostaInvalida(pergunta, formatoEsperado);
        }
    }

    default Number numero(Object resposta, Pergunta pergunta, String formatoEsperado) {
        try {
            return (Number) resposta;
        } catch (ClassCastException cce) {
            throw respostaInvalida(pergunta, formatoEsperado);
        }
    }

    default Boolean booleano(Object resposta, Pergunta pergunta, String formatoEsperado) {
        try {
            return (Boolean) resposta;
        } catch (ClassCastException cce) {
            throw respostaInvalida(pergunta, formatoEsperado);
        }
    }

    default List<?> lista(Object resposta, Pergunta pergunta, String formatoEsperado) {
        try {
            return (List<?>) resposta;
        } catch (ClassCastException cce) {
            throw respostaInvalida(pergunta, formatoEsperado);
        }
    }

    default InvalidRequestException respostaInvalida(Pergunta pergunta, String formatoEsperado) {
        return new InvalidRequestException("A resposta da pergunta '" + pergunta.getTitulo() + "' deve ser " + formatoEsperado);
    }
}
