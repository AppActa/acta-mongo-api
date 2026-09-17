package br.com.acta.common.utils;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.embedded.Porque;
import br.com.acta.document.enums.CategoriaIshikawa;
import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.document.enums.TipoFormulario;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversorTest {
    @Test
    void deveConverterCausasRecebidasComoJson() {
        Object valor = List.of(Map.of(
                "categoria", "MAQUINA",
                "descricao", "Sensor instável"
        ));

        List<CausaIshikawa> causas = ConversorObject.toCausasIshikawa(valor);

        assertEquals(CategoriaIshikawa.MAQUINA, causas.get(0).getCategoria());
        assertEquals("Sensor instável", causas.get(0).getDescricao());
    }

    @Test
    void deveConverterPorquesRecebidosComoJson() {
        Object valor = List.of(Map.of(
                "ordem", 1,
                "pergunta", "Por quê?",
                "resposta", "Porque sim"
        ));

        List<Porque> porques = ConversorObject.toPorques(valor);

        assertEquals(1, porques.get(0).getOrdem());
        assertEquals("Por quê?", porques.get(0).getPergunta());
        assertEquals("Porque sim", porques.get(0).getResposta());
    }

    @Test
    void deveRejeitarValorQueNaoSejaLista() {
        assertThrows(InvalidRequestException.class,
                () -> ConversorObject.toPorques(Map.of("ordem", 1)));
    }

    @Test
    void deveRejeitarItemInvalidoDaLista() {
        assertThrows(InvalidRequestException.class,
                () -> ConversorObject.toPorques(List.of(Map.of("ordem", "inválida"))));
    }

    @Test
    void deveConverterCamposDoFormulario() {
        UUID idIshikawa = UUID.randomUUID();
        Object perguntas = List.of(Map.of(
                "titulo", "Pergunta",
                "tipo", "TEXTO",
                "obrigatoria", true,
                "opcoes", List.of()
        ));

        List<Pergunta> resultado = ConversorObject.toPerguntas(perguntas);

        assertEquals("Pergunta", resultado.get(0).getTitulo());
        assertEquals(TipoFormulario.ISHIKAWA, ConversorObject.toTipoFormulario("ISHIKAWA"));
        assertEquals(StatusFormulario.ATIVO, ConversorObject.toStatusFormulario("ATIVO"));
        assertEquals(idIshikawa, ConversorObject.toUUID(idIshikawa.toString(), "idIshikawa"));
        assertEquals(List.of(101L, 102L), ConversorObject.toLongList(List.of(101, 102), "destinatarios"));
        assertEquals(Instant.parse("2026-08-23T18:00:00Z"),
                ConversorObject.toInstant("2026-08-23T18:00:00Z", "publicadoEm"));
    }

    @Test
    void deveRejeitarConversaoInvalidaDoFormulario() {
        assertThrows(InvalidRequestException.class, () -> ConversorObject.toTipoFormulario("DESCONHECIDO"));
        assertThrows(InvalidRequestException.class, () -> ConversorObject.toLongList("101", "destinatarios"));
        assertThrows(InvalidRequestException.class, () -> ConversorObject.toInstant("data inválida", "publicadoEm"));
    }

    @Test
    void deveConverterRespostasDeFormulario() {
        UUID idPergunta = UUID.randomUUID();

        List<RespostaPerguntaRequestDTO> respostas = ConversorObject.toRespostasPergunta(List.of(Map.of(
                "idPergunta", idPergunta.toString(),
                "resposta", "Resposta"
        )));

        assertEquals(idPergunta, respostas.get(0).idPergunta());
        assertEquals("Resposta", respostas.get(0).resposta());
    }
}
