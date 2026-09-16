package br.com.acta.common.validation.strategy;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoCpfStrategy implements ValidacaoRespostaStrategy {
    private final CPFValidator cpfValidator = new CPFValidator();

    @Override
    public TipoResposta getTipo() {
        return TipoResposta.CPF;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String cpf = texto(resposta, pergunta, "um CPF válido");
        if (!cpfValidator.invalidMessagesFor(cpf).isEmpty())
            throw respostaInvalida(pergunta, "um CPF válido");
    }
}
