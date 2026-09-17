package br.com.acta.service;

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
        assertEquals(UP, new HealthService(template).verificar().status());
    }

    @Test
    void deveRetornarDownQuandoMongoFalhar() {
        MongoTemplate template = mock(MongoTemplate.class);
        when(template.getDb()).thenThrow(new RuntimeException("indisponível"));
        var resposta = new HealthService(template).verificar();
        assertEquals(DOWN, resposta.status());
        assertEquals(DOWN, resposta.banco());
    }
}
