package br.com.acta.service;

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
    private final String mensagemSucesso = "O CATO verificou: a API e o banco estão funcionando!";
    private final String mensagemErro = "O CATO encontrou um problema na conexão com o banco";
    private final MongoTemplate mongoTemplate;

    public HealthResponseDTO verificar() {
        try {
            testarBanco();
            return new HealthResponseDTO(HealthStatus.UP, HealthStatus.UP, mensagemSucesso, OffsetDateTime.now());
        } catch (Exception e) {
            return new HealthResponseDTO(HealthStatus.DOWN, HealthStatus.DOWN, mensagemErro, OffsetDateTime.now());
        }
    }

    private void testarBanco() {
        mongoTemplate.getDb().runCommand(new Document("ping", 1));
    }
}