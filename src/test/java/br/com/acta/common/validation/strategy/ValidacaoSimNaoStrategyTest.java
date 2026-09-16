package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoSimNaoStrategyTest {
    private ValidacaoSimNaoStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoSimNaoStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Confirma?");
    }

    @Test void deveRepresentarTipoSimNao() { assertEquals(TipoResposta.SIM_NAO, strategy.getTipo()); }
    @Test void deveAceitarVerdadeiro() { assertDoesNotThrow(() -> strategy.validar(pergunta, true)); }
    @Test void deveAceitarFalso() { assertDoesNotThrow(() -> strategy.validar(pergunta, false)); }
    @Test void deveRejeitarRespostaQueNaoSejaBooleana() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "sim")); }
}
