package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoDataStrategyTest {
    private ValidacaoDataStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoDataStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Data");
    }

    @Test void deveRepresentarTipoData() { assertEquals(TipoResposta.DATA, strategy.getTipo()); }
    @Test void deveAceitarDataIsoValida() { assertDoesNotThrow(() -> strategy.validar(pergunta, "2026-09-14")); }
    @Test void deveRejeitarDataInexistente() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "2026-02-30")); }
    @Test void deveRejeitarDataForaDoFormatoIso() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "14/09/2026")); }
    @Test void deveRejeitarRespostaQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 20260914)); }
}
