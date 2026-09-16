package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoTelefoneStrategyTest {
    private ValidacaoTelefoneStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoTelefoneStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Telefone");
    }

    @Test void deveRepresentarTipoTelefone() { assertEquals(TipoResposta.TELEFONE, strategy.getTipo()); }
    @Test void deveAceitarCelularComFormatacao() { assertDoesNotThrow(() -> strategy.validar(pergunta, "(11) 98765-4321")); }
    @Test void deveAceitarTelefoneComDezDigitos() { assertDoesNotThrow(() -> strategy.validar(pergunta, "1133334444")); }
    @Test void deveRejeitarTelefoneSemDdd() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "98765-4321")); }
    @Test void deveRejeitarRespostaQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 11987654321L)); }
}
