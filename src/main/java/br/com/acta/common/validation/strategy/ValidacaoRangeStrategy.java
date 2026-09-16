package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValidacaoRangeStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.RANGE;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        Number valor = numero(resposta, pergunta, "um valor numérico");

        try {
            new BigDecimal(valor.toString());
        } catch (NumberFormatException ex) {
            throw respostaInvalida(pergunta, "um valor numérico");
        }
    }
}
