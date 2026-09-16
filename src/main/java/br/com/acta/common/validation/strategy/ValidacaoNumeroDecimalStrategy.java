package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValidacaoNumeroDecimalStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.NUMERO_DECIMAL;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        Number valor = numero(resposta, pergunta, "um número decimal");
        if (!decimalValido(valor)) throw respostaInvalida(pergunta, "um número decimal");
    }

    private boolean decimalValido(Number numero) {
        try {
            new BigDecimal(numero.toString());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
