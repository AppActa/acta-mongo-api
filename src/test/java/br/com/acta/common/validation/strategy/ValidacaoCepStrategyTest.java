package br.com.acta.common.validation.strategy;

import br.com.acta.common.client.ViaCepClient;
import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.validation.RespostaFormularioValidator;
import br.com.acta.document.Formulario;
import br.com.acta.document.embedded.Pergunta;
import br.com.acta.document.enums.TipoResposta;
import br.com.acta.dto.resposta_formulario.RespostaPerguntaRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidacaoCepStrategyTest {
    private final ViaCepClient client = mock(ViaCepClient.class);
    private final ValidacaoCepStrategy strategy = new ValidacaoCepStrategy(client);

    @Test
    void deveRejeitarCepForaDoFormatoSemConsultarViaCep() {
        Pergunta pergunta = new Pergunta();
        pergunta.setTitulo("CEP");
        for (Object resposta : List.of("01001-000", "1234567", "abcdefgh", 1001000, Map.of("cep", "01001000")))
            assertThrows(InvalidRequestException.class, () -> strategy.validar(pergunta, resposta));
        verifyNoInteractions(client);
    }

    @Test
    void devePrepararEnderecoAPartirApenasDoCepPreservandoOutrasRespostas() {
        Pergunta cep = new Pergunta();
        cep.setId(UUID.randomUUID());
        cep.setTipo(TipoResposta.CEP);
        Pergunta texto = new Pergunta();
        texto.setId(UUID.randomUUID());
        texto.setTipo(TipoResposta.TEXTO);
        Formulario formulario = new Formulario();
        formulario.setPerguntas(List.of(cep, texto));
        Map<String, String> endereco = Map.of("cep", "01001000", "uf", "SP", "cidade", "São Paulo", "bairro", "Sé", "logradouro", "Praça da Sé");
        when(client.buscarEndereco("01001000")).thenReturn(endereco);
        RespostaFormularioValidator validator = new RespostaFormularioValidator(List.of(strategy, new ValidacaoTextoStrategy()));
        List<RespostaPerguntaRequestDTO> respostas = List.of(
                new RespostaPerguntaRequestDTO(cep.getId(), "01001000"),
                new RespostaPerguntaRequestDTO(texto.getId(), "Empresa"));

        validator.validar(formulario, respostas);
        List<RespostaPerguntaRequestDTO> preparadas = validator.preparar(formulario, respostas);

        assertEquals(endereco, preparadas.get(0).resposta());
        assertEquals("Empresa", preparadas.get(1).resposta());
        assertEquals("01001000", respostas.get(0).resposta());
        verify(client).buscarEndereco("01001000");
    }
}
