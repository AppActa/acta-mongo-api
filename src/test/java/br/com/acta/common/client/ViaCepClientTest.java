package br.com.acta.common.client;

import br.com.acta.common.handler.exception.InvalidRequestException;
import br.com.acta.common.handler.exception.ViaCepException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ViaCepClientTest {
    private MockRestServiceServer server;
    private ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://viacep.com.br/ws/");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient("https://viacep.com.br/ws/",
                Duration.ofSeconds(1), Duration.ofSeconds(1));
        ReflectionTestUtils.setField(client, "restClient", builder.build());
    }

    @Test
    void deveConverterLocalidadeParaCidadeEManterCepSemMascara() {
        String body = """
                {"cep":"01001-000","uf":"SP","localidade":"São Paulo",
                 "bairro":"Sé","logradouro":"Praça da Sé","ibge":"3550308"}
                """;
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
        assertEquals(Map.of("cep", "01001000", "uf", "SP", "cidade", "São Paulo",
                "bairro", "Sé", "logradouro", "Praça da Sé"), client.buscarEndereco("01001000"));
        server.verify();
    }

    @Test
    void deveRejeitarCepInexistente() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));
        assertThrows(InvalidRequestException.class, () -> client.buscarEndereco("01001000"));
        server.verify();
    }

    @Test
    void deveTratarFalhaDoServicoEEnderecoIncompleto() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertThrows(ViaCepException.class, () -> client.buscarEndereco("01001000"));
        assertThrows(ViaCepException.class, () -> client.buscarEndereco("01001000"));
        server.verify();
    }
}