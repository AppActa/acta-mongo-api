package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoTextoStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.TEXTO;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String valor = texto(resposta, pergunta, "um texto não vazio");
        if (valor.isBlank()) throw respostaInvalida(pergunta, "um texto não vazio");
    }
}
