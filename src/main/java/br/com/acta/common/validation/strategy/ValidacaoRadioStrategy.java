package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoRadioStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.RADIO;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String opcao = texto(resposta, pergunta, "uma das opções disponíveis");
        if (!pergunta.getOpcoes().contains(opcao))
            throw respostaInvalida(pergunta, "uma das opções disponíveis");
    }
}
