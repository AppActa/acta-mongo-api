package br.com.acta.common.validation.strategy;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoCheckboxStrategyTest {
    private ValidacaoCheckboxStrategy strategy;
    private Pergunta pergunta;

    @BeforeEach
    void setUp() {
        strategy = new ValidacaoCheckboxStrategy();
        pergunta = new Pergunta();
        pergunta.setTitulo("Áreas afetadas");
        pergunta.setOpcoes(List.of("Qualidade", "Produção", "Logística"));
    }

    @Test void deveRepresentarTipoCheckbox() { assertEquals(TipoResposta.CHECKBOX, strategy.getTipo()); }
    @Test void deveAceitarOpcoesDisponiveis() { assertDoesNotThrow(() -> strategy.validar(pergunta, List.of("Qualidade", "Logística"))); }
    @Test void deveRejeitarListaVazia() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, List.of())); }
    @Test void deveRejeitarOpcaoIndisponivel() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, List.of("Financeiro"))); }
    @Test void deveRejeitarItemQueNaoSejaTexto() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, List.of(1))); }
    @Test void deveRejeitarRespostaQueNaoSejaLista() { assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, "Qualidade")); }
}
