package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoNumeroInteiroStrategyTest {
    private ValidacaoNumeroInteiroStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoNumeroInteiroStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Quantidade");
    }

    @Test void deveRepresentarTipoNumeroInteiro() { assertEquals(TipoResposta.NUMERO_INTEIRO, strategy.getTipo()); }
    @Test void deveAceitarNumeroInteiro() { assertDoesNotThrow(() -> strategy.validar(pergunta, 42)); }
    @Test void deveAceitarDecimalEquivalenteAInteiro() { assertDoesNotThrow(() -> strategy.validar(pergunta, 42.0)); }
    @Test void deveRejeitarNumeroComParteDecimal() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 42.5)); }
    @Test void deveRejeitarRespostaQueNaoSejaNumero() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "42")); }
}
