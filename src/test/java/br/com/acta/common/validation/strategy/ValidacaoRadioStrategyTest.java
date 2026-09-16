package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoRadioStrategyTest {
    private ValidacaoRadioStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoRadioStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Prioridade");
        pergunta.setOpcoes(List.of("Baixa", "Média", "Alta"));
    }

    @Test void deveRepresentarTipoRadio() { assertEquals(TipoResposta.RADIO, strategy.getTipo()); }
    @Test void deveAceitarOpcaoDisponivel() { assertDoesNotThrow(() -> strategy.validar(pergunta, "Alta")); }
    @Test void deveRejeitarOpcaoIndisponivel() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "Urgente")); }
    @Test void deveRejeitarRespostaQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, 1)); }
}
