package br.com.acta.common.validation;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.validation.strategy.ValidacaoRespostaStrategy;
import br.com.acta.document.Formulario;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RespostaFormularioValidatorTest {
    private ValidacaoRespostaStrategy strategy;
    private RespostaFormularioValidator validator;
    private UUID idPergunta;
    private Formulario formulario;

    @BeforeEach
    void setUp() {
        strategy = mock(ValidacaoRespostaStrategy.class);
        when(strategy.getTipo()).thenReturn(TipoResposta.TEXTO);
        validator = new RespostaFormularioValidator(List.of(strategy));
        idPergunta = UUID.randomUUID();
        Pergunta pergunta = new Pergunta();
        pergunta.setId(idPergunta);
        pergunta.setTitulo("Descrição");
        pergunta.setTipo(TipoResposta.TEXTO);
        pergunta.setObrigatoria(true);
        formulario = new Formulario();
        formulario.setPerguntas(List.of(pergunta));
    }

    @Test
    void deveDelegarRespostaValidaParaStrategyDoTipo() {
        validator.validar(formulario, List.of(new RespostaPerguntaRequestDTO(idPergunta, "ok")));
        verify(strategy).validar(formulario.getPerguntas().get(0), "ok");
    }

    @Test
    void deveRejeitarPerguntaDuplicada() {
        RespostaPerguntaRequestDTO resposta = new RespostaPerguntaRequestDTO(idPergunta, "ok");
        assertThrows(InvalidRequestException.class, () -> validator.validar(formulario, List.of(resposta, resposta)));
    }

    @Test
    void deveRejeitarPerguntaQueNaoPertenceAoFormulario() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validar(formulario, List.of(new RespostaPerguntaRequestDTO(UUID.randomUUID(), "ok"))));
    }

    @Test
    void deveRejeitarRespostaNulaEPerguntaObrigatoriaAusente() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validar(formulario, List.of(new RespostaPerguntaRequestDTO(idPergunta, null))));
        assertThrows(InvalidRequestException.class, () -> validator.validar(formulario, List.of()));
    }

    @Test
    void deveRejeitarTipoSemStrategyImplementada() {
        formulario.getPerguntas().get(0).setTipo(TipoResposta.ARQUIVO);
        assertThrows(InvalidRequestException.class,
                () -> validator.validar(formulario, List.of(new RespostaPerguntaRequestDTO(idPergunta, "arquivo"))));
    }
}
