package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoEmailStrategyTest {
    private ValidacaoEmailStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoEmailStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("E-mail");
    }

    @Test void deveRepresentarTipoEmail() { assertEquals(TipoResposta.EMAIL, strategy.getTipo()); }
    @Test void deveAceitarEmailValido() { assertDoesNotThrow(() -> strategy.validar(pergunta, "usuario@acta.com")); }
    @Test void deveRejeitarEmailInvalido() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "usuario@acta")); }
    @Test void deveRejeitarRespostaQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 10)); }
}
