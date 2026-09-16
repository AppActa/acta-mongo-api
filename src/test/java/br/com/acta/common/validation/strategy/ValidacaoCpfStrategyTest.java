package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacaoCpfStrategyTest {
    private ValidacaoCpfStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoCpfStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Informe o CPF");
        pergunta.setTipo(TipoResposta.CPF);
    }

    @Test
    void deveRepresentarTipoCpf() {
        assertEquals(TipoResposta.CPF, strategy.getTipo());
    }

    @Test
    void deveAceitarCpfValido() {
        assertDoesNotThrow(() -> strategy.validar(pergunta, "52998224725"));
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorInvalido() {
        assertThrows(InvalidRequestException.class,
                () -> strategy.validar(pergunta, "52998224724"));
    }

    @Test
    void deveRejeitarCpfComDigitosRepetidos() {
        assertThrows(InvalidRequestException.class,
                () -> strategy.validar(pergunta, "11111111111"));
    }

    @Test
    void deveRejeitarRespostaQueNaoSejaTexto() {
        assertThrows(InvalidRequestException.class,
                () -> strategy.validar(pergunta, 52998224725L));
    }
}
