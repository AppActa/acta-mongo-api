package br.com.acta.service;

import br.com.acta.common.client.FcmApiClient;
import br.com.acta.common.client.AuthApiClient;
import br.com.acta.common.client.PgApiClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static br.com.acta.dto.health.HealthStatus.DOWN;
import static br.com.acta.dto.health.HealthStatus.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthServiceTest {
    @Test
    void deveRetornarUpQuandoMongoResponder() {
        MongoTemplate template = mock(MongoTemplate.class);
        MongoDatabase database = mock(MongoDatabase.class);
        when(template.getDb()).thenReturn(database);
        PgApiClient pg = mock(PgApiClient.class);
        AuthApiClient auth = mock(AuthApiClient.class);
        FcmApiClient fcm = mock(FcmApiClient.class);
        when(pg.verificarSaude()).thenReturn(true);
        when(auth.verificarSaude()).thenReturn(true);
        when(fcm.verificarSaude()).thenReturn(true);
        assertEquals(UP, new HealthService(template, pg, auth, fcm).verificar().status());
    }

    @Test
    void deveRetornarDownQuandoMongoFalhar() {
        MongoTemplate template = mock(MongoTemplate.class);
        when(template.getDb()).thenThrow(new RuntimeException("indisponível"));
        var resposta = new HealthService(template, mock(PgApiClient.class), mock(AuthApiClient.class), mock(FcmApiClient.class)).verificar();
        assertEquals(DOWN, resposta.status());
        assertEquals(DOWN, resposta.banco());
    }
}
