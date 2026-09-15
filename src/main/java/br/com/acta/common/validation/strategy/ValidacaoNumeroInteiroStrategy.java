package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoNumeroInteiroStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.NUMERO_INTEIRO;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        Number valor = numero(resposta, pergunta, "um número inteiro");
        if (!inteiro(valor)) throw respostaInvalida(pergunta, "um número inteiro");
    }

    private boolean inteiro(Number numero) {
        return numero.doubleValue() % 1 == 0;
    }
}
