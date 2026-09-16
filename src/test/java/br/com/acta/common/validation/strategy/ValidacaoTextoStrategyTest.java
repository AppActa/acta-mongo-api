package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoTextoStrategyTest {
    private ValidacaoTextoStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoTextoStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Comentário");
    }

    @Test void deveRepresentarTipoTexto() { assertEquals(TipoResposta.TEXTO, strategy.getTipo()); }
    @Test void deveAceitarTextoPreenchido() { assertDoesNotThrow(() -> strategy.validar(pergunta, "Resposta")); }
    @Test void deveRejeitarTextoEmBranco() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "   ")); }
    @Test void deveRejeitarRespostaQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 10)); }
}
