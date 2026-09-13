package br.com.acta.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record HealthResponseDTO(
        HealthStatus status,
        HealthStatus banco,
        String mensagem,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime verificadoEm
) {
}
