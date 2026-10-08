package br.com.acta.common.validation.strategy;

import br.com.acta.common.client.ViaCepClient;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoCepStrategy implements ValidacaoRespostaStrategy {
    private final ViaCepClient viaCepClient;

    public ValidacaoCepStrategy(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @Override
    public TipoResposta getTipo() {
        return TipoResposta.CEP;
    }

    @Override
    public void validar(Pergunta pergunta, Object resposta) {
        String cep = texto(resposta, pergunta, "um CEP com oito dígitos");
        if (cep == null || !cep.matches("[0-9]{8}"))
            throw respostaInvalida(pergunta, "um CEP com oito dígitos");
    }

    @Override
    public Object preparar(Object resposta) {
        return viaCepClient.buscarEndereco((String) resposta);
    }
}