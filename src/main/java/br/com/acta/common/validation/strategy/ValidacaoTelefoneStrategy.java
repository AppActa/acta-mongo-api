package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoTelefoneStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.TELEFONE;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String telefone = texto(resposta, pergunta, "um telefone válido com DDD");
        if (!telefone.replaceAll("\\D", "").matches("\\d{10,11}"))
            throw respostaInvalida(pergunta, "um telefone válido com DDD");
    }
}
