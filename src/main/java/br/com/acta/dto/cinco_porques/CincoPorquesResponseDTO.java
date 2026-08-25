package br.com.acta.dto.cinco_porques;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CincoPorquesResponseDTO(
        UUID id,
        UUID idIshikawa,
        UUID idCausaIshikawa,
        Long idCausaRaiz,
        String hipotese,
        List<PorqueResponseDTO> porques,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime criadoEm,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime atualizadoEm
) {
}
