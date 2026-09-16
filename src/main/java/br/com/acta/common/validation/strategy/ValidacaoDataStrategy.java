package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class ValidacaoDataStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.DATA;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String data = texto(resposta, pergunta, "uma data no formato ISO yyyy-MM-dd");
        try {
            LocalDate.parse(data);
        } catch (DateTimeParseException ex) {
            invalida(pergunta);
        }
    }

    private void invalida(Pergunta pergunta) {
        throw new InvalidRequestException("A resposta da pergunta '" + pergunta.getTitulo() + "' deve ser uma data no formato ISO yyyy-MM-dd");
    }
}
