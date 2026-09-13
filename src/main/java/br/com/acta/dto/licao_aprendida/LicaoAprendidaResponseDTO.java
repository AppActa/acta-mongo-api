package br.com.acta.dto.licao_aprendida;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record LicaoAprendidaResponseDTO(
        UUID id,
        Long idEmpresa,
        Long idCiclo,
        String titulo,
        String licao,
        List<String> tags,
        String faseOrigem,
        String resultado,
        String severidade,
        String area,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime criadoEm,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime atualizadoEm
) {
}
