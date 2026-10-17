package br.com.acta.common.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FcmApiClientTest {
    @Test
    void deveEnviarEnvelopeCompativelEIgnorarFalhaHttp() {
        RestClient.Builder builder = RestClient.builder();
        FcmApiClient client = new FcmApiClient(builder, "http://localhost:8082", "test-token", java.time.Duration.ofSeconds(1), java.time.Duration.ofSeconds(1));
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID id = UUID.randomUUID();
        Instant instante = Instant.parse("2026-10-17T12:30:45Z");
        server.expect(requestTo("http://localhost:8082/internal/v1/eventos"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", "test-token"))
                .andExpect(content().json("""
                        {
                          "idEvento":"FORMULARIO_PUBLICADO:%s:2026-10-17T12:30:45Z",
                          "tipoEvento":"FORMULARIO_PUBLICADO",
                          "ocorridoEm":"2026-10-17T12:30:45Z",
                          "idEmpresa":10,
                          "idsDestinatarios":[101,102],
                          "tipoEntidade":"FORMULARIO",
                          "idEntidade":"%s",
                          "deepLink":"acta://formularios/%s",
                          "chaveDeduplicacao":"FORMULARIO_PUBLICADO:%s:2026-10-17T12:30:45Z"
                        }
                        """.formatted(id, id, id, id)))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("offline").contentType(org.springframework.http.MediaType.TEXT_PLAIN));

        assertDoesNotThrow(() -> client.publicarFormularioPublicado(id, 10L, List.of(101L, 102L), instante));
        server.verify();
    }
}
