package br.com.acta.service;

import br.com.acta.common.client.FcmApiClient;
import br.com.acta.common.client.AuthApiClient;
import br.com.acta.common.client.PgApiClient;
import br.com.acta.dto.health.HealthResponseDTO;
import br.com.acta.dto.health.HealthStatus;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class HealthService {
    private final MongoTemplate mongoTemplate;
    private final PgApiClient pgApiClient;
    private final AuthApiClient authApiClient;
    private final FcmApiClient fcmApiClient;

    public HealthResponseDTO verificar() {
        HealthStatus banco;
        try {
            testarBanco();
            banco = HealthStatus.UP;
        } catch (Exception e) {
            banco = HealthStatus.DOWN;
        }

        HealthStatus pg = pgApiClient.verificarSaude() ? HealthStatus.UP : HealthStatus.DOWN;
        HealthStatus auth = authApiClient.verificarSaude() ? HealthStatus.UP : HealthStatus.DOWN;
        HealthStatus fcm = fcmApiClient.verificarSaude() ? HealthStatus.UP : HealthStatus.DOWN;
        HealthStatus status = banco == HealthStatus.UP && pg == HealthStatus.UP && auth == HealthStatus.UP && fcm == HealthStatus.UP ? HealthStatus.UP : HealthStatus.DOWN;
        String mensagem = status == HealthStatus.UP ? "O CATO verificou: a API, o banco, a PG, a Auth e a FCM estão funcionando!" : "O CATO encontrou uma dependência indisponível; consulte banco, pg, auth e fcm";
        return new HealthResponseDTO(status, banco, pg, auth, fcm, mensagem, OffsetDateTime.now());
    }

    private void testarBanco() {
        mongoTemplate.getDb().runCommand(new Document("ping", 1));
    }
}