package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoSimNaoStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.SIM_NAO;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        booleano(resposta, pergunta, "verdadeira ou falsa");
    }
}
