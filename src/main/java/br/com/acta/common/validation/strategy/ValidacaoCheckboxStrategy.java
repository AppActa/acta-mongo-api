package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidacaoCheckboxStrategy implements ValidacaoRespostaStrategy {
    @Override
    public TipoResposta getTipo() {
        return TipoResposta.CHECKBOX;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        List<?> opcoesSelecionadas = lista(resposta, pergunta, "uma lista com opções disponíveis");
        if (opcoesSelecionadas.isEmpty() || pergunta.getOpcoes() == null)
            throw respostaInvalida(pergunta, "uma lista com opções disponíveis");

        try {
            for (Object opcao : opcoesSelecionadas) {
                if (!pergunta.getOpcoes().contains((String) opcao))
                    throw respostaInvalida(pergunta, "uma lista com opções disponíveis");
            }
        } catch (ClassCastException ex) {
            throw respostaInvalida(pergunta, "uma lista com opções disponíveis");
        }
    }
}
