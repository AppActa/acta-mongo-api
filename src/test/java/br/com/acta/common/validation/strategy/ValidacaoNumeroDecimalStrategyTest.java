package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoNumeroDecimalStrategyTest {
    private ValidacaoNumeroDecimalStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoNumeroDecimalStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Valor");
    }

    @Test void deveRepresentarTipoNumeroDecimal() { assertEquals(TipoResposta.NUMERO_DECIMAL, strategy.getTipo()); }
    @Test void deveAceitarNumeroDecimal() { assertDoesNotThrow(() -> strategy.validar(pergunta, 10.5)); }
    @Test void deveAceitarNumeroInteiroComoDecimal() { assertDoesNotThrow(() -> strategy.validar(pergunta, 10)); }
    @Test void deveRejeitarNumeroNaoFinito() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, Double.NaN)); }
    @Test void deveRejeitarRespostaQueNaoSejaNumero() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "10.5")); }
}
