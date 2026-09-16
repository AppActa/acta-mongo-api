package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoRangeStrategyTest {
    private ValidacaoRangeStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoRangeStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Nota");
    }

    @Test void deveRepresentarTipoRange() { assertEquals(TipoResposta.RANGE, strategy.getTipo()); }
    @Test void deveAceitarValorNumerico() { assertDoesNotThrow(() -> strategy.validar(pergunta, 7.5)); }
    @Test void deveRejeitarNumeroNaoFinito() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, Double.POSITIVE_INFINITY)); }
    @Test void deveRejeitarRespostaQueNaoSejaNumero() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "7.5")); }
}
