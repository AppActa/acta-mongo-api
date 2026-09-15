package br.com.acta.common.validation.strategy;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidacaoEmailStrategy implements ValidacaoRespostaStrategy {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public TipoResposta getTipo() {
        return TipoResposta.EMAIL;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String email = texto(resposta, pergunta, "um e-mail válido");
        if (!EMAIL.matcher(email).matches()) throw respostaInvalida(pergunta, "um e-mail válido");
    }
}
